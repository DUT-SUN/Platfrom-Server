package com.example.blogssm.service;

import com.alibaba.fastjson.JSON;
import com.example.blogssm.entity.ArticleInfo;
import com.example.blogssm.entity.PageResult;
import com.example.blogssm.entity.RequestParams;
import com.example.blogssm.entity.UserInfo;
import com.example.blogssm.entity.vo.ArticleinfoVO;
import com.example.blogssm.entity.vo.BlogDoc;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/03/28  12:17
 */
@Service
public class ESearchService {
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private RestHighLevelClient client;

    //过滤函数算分函数逻辑
    private FunctionScoreQueryBuilder buildQuery(RequestParams params, SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //准备DSL
        String key = params.getKey();
        System.out.println(key);
        if (key == null || "".equals(key)) {
            boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        } else {
            boolQueryBuilder.must(QueryBuilders.matchQuery("all", key));
        }
        //这边的话要进行filter掉那些state字段不希望展现到大厅的博客文章
//        boolQueryBuilder.filter(QueryBuilders.termQuery("state", 0));
        //Function score 这里只是对于函数算分的逻辑，这里应该是对于收藏量及其点击量的计算
//         参数   ScoreFunctionBuilder function
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(boolQueryBuilder, new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        ScoreFunctionBuilders.fieldValueFactorFunction("rcount").modifier(FieldValueFactorFunction.Modifier.LOG1P).factor(0.1f)
                ),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        ScoreFunctionBuilders.fieldValueFactorFunction("favorite").modifier(FieldValueFactorFunction.Modifier.LOG1P).factor(0.3f)
                ),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        ScoreFunctionBuilders.fieldValueFactorFunction("comment").modifier(FieldValueFactorFunction.Modifier.LOG1P).factor(0.2f)
                ),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.termQuery("isAD", true),
                        ScoreFunctionBuilders.weightFactorFunction(10)
                )
        }).scoreMode(FunctionScoreQuery.ScoreMode.SUM);
        ;
        //给钱文章直接指定
        return functionScoreQueryBuilder;
    }
    //

    private PageResult handleResponse(SearchResponse response) {
        //解析响应
        SearchHits searchHits = response.getHits();
        Long total = searchHits.getTotalHits().value;
        SearchHit[] hits = searchHits.getHits();
        List<BlogDoc> blogs = new ArrayList<>();
        for (SearchHit hit : hits) {
            String source = hit.getSourceAsString();
            BlogDoc blogDoc = JSON.parseObject(source, BlogDoc.class);

            // 获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlightTitle = highlightFields.get("all");
            if (highlightTitle != null) {
                Text[] fragments = highlightTitle.fragments();
                String highlightedTitle = Arrays.stream(fragments)
                        .map(Text::string)
                        .collect(Collectors.joining(","));
                // 这里的highlightedTitle就是高亮后的标题
                blogDoc.setContent(highlightedTitle); // 假设BlogDoc有setTitle方法
            }

            blogs.add(blogDoc);
        }
        return new PageResult(total, blogs);
    }


    //用来处理keyword搜索出来的所有文章数据，并返回
    public PageResult filters(RequestParams params) {
        try {
            //准备request
            SearchRequest searchRequest = new SearchRequest("blog");
            //过滤
            FunctionScoreQueryBuilder boolQueryBuilder = buildQuery(params, searchRequest);
            searchRequest.source().query(boolQueryBuilder);//过滤后的数据
            //添加高亮设置
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field("all");
            highlightTitle.preTags("<em>");
            highlightTitle.postTags("</em>");
            highlightBuilder.field(highlightTitle);
            searchRequest.source().highlighter(highlightBuilder);
            //分页
            int page = params.getPage();
            int size = params.getSize();
            searchRequest.source().from((page - 1) * size).size(size);
            //发送请求，得到响应
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            return handleResponse(response);
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    //在搜索过程中的自动补全
    public List<String> getSuggestions(String prefix) {
        try {
            //函数为了补全，返回关键字数组
            SearchRequest request = new SearchRequest("blog");
            request.source().suggest(new SuggestBuilder().addSuggestion(
                    "suggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(prefix)
                            .size(6)
                            .skipDuplicates(true)
            ));
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Suggest suggestions = response.getSuggest();
            CompletionSuggestion suggestion = suggestions.getSuggestion("suggestions");
            List<String> list = new ArrayList<>();
            for (CompletionSuggestion.Entry.Option option : suggestion.getOptions()) {
                list.add(option.getText().toString());
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteById(Integer id) {
        try {
            //准备request
            //发送请求
            DeleteRequest deleteRequest = new DeleteRequest("blog", id.toString());
            client.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertById(Integer id) {
        try {
            //获取数据库信息
            ArticleinfoVO articleInfo = articleService.getDetail(id);
            UserInfo user = userService.getUserById(articleInfo.getUid());
            BlogDoc blogDoc;
            if (articleInfo != null && user != null) {
                blogDoc = new BlogDoc(user, articleInfo);
            } else {
                return;
            }
            UpdateRequest request = new UpdateRequest("blog", blogDoc.getId().toString());

            // 准备参数
            request.doc(JSON.toJSONString(blogDoc), XContentType.JSON);

            // 如果文档不存在，使用upsert方法创建它
            request.upsert(JSON.toJSONString(blogDoc), XContentType.JSON);

            // 发起请求
            client.update(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PageResult getTagList(String type) {
        try {
            // 创建 SearchRequest
            SearchRequest searchRequest = new SearchRequest("blog");

            // 创建查询
            MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("type", type);
            searchRequest.source().query(queryBuilder);

            // 发送请求，获取响应
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

            // 处理响应
            SearchHits hits = response.getHits();
            Long total = hits.getTotalHits().value;
            List<BlogDoc> blogDocs = Arrays.stream(hits.getHits())
                    .map(hit -> {
                        // 将每个 hit 转换为 BlogDoc 对象
                        String sourceAsString = hit.getSourceAsString();
                        BlogDoc blogDoc = JSON.parseObject(sourceAsString, BlogDoc.class);
                        return blogDoc;
                    })
                    .collect(Collectors.toList());

            return new PageResult(total, blogDocs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
