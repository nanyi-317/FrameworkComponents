package com.yibao.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yibao.elasticsearch.entity.Product;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedDoubleTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootTest
class RestHighLevelClientTest {
    // 对象
    private final RestHighLevelClient elasticsearchClient;

    @Autowired
    public RestHighLevelClientTest(RestHighLevelClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    /**
     * 测试：删除索引
     */
    @Test
    public void test2() throws IOException {
        AcknowledgedResponse products = elasticsearchClient.indices().delete(new DeleteIndexRequest("products"), RequestOptions.DEFAULT);
        System.out.println(products.isAcknowledged());
    }


    /**
     * 测试：创建索引 & 映射
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("products");
        createIndexRequest.mapping("{\n" +
                "    \"properties\": {\n" +
                "      \"id\":{\n" +
                "        \"type\": \"integer\"\n" +
                "      },\n" +
                "      \"title\":{\n" +
                "        \"type\": \"keyword\"\n" +
                "      },\n" +
                "      \"price\":{\n" +
                "        \"type\": \"double\"\n" +
                "      },\n" +
                "      \"created_at\":{\n" +
                "        \"type\": \"date\"\n" +
                "      },\n" +
                "      \"description\":{\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"ik_max_word\"\n" +
                "      }\n" +
                "    }\n" +
                "  }", XContentType.JSON);
        CreateIndexResponse response = elasticsearchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged());   // 是否创建成功
        elasticsearchClient.close();    // 关闭资源

    }

    /**
     * 测试：添加一条文档
     */
    @Test
    public void test3() throws IOException {
        IndexRequest indexRequest = new IndexRequest("products");
        indexRequest.id("3")  // 手动指定 id
                .source("{\n" +
                        "  \"id\":3,\n" +
                        "  \"title\":\"标题222222\",\n" +
                        "  \"price\":33333,\n" +
                        "  \"created_at\":\"2022-03-28\",\n" +
                        "  \"description\":\"描述333333\"\n" +
                        "}", XContentType.JSON);
        IndexResponse index = elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(index.status());
    }

    /**
     * 测试：更新文档
     */
    @Test
    public void test4() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("products", "1");
        updateRequest.doc("{\"description\":\"描述\"}", XContentType.JSON);
        UpdateResponse update = elasticsearchClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update.status());
    }

    /**
     * 测试 ：删除文档
     */
    @Test
    public void test5() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("products", "1");
        DeleteResponse delete = elasticsearchClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete.status());
    }


    /**
     * 测试：基于 id 的方式查询文档
     */
    @Test
    public void test6() throws IOException {
        GetRequest getRequest = new GetRequest("products", "1");
        GetResponse getResponse = elasticsearchClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString());
    }

    /**
     * 测试：查询所有
     */
    @Test
    public void test7(QueryBuilder queryBuilder) throws IOException {
        // 指定搜索索引
        SearchRequest searchRequest = new SearchRequest("products");
        // 设定条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(queryBuilder);
        // 指定查询条件
        searchRequest.source(builder);
        // 搜索
        SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        // 解析结果
        System.out.println("总条数 --- " + searchResponse.getHits().getTotalHits().value);
        System.out.println("最大的分 --- " + searchResponse.getHits().getMaxScore());

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println("id -- " + hit.getId() + " -- 结果集 -- " + hit.getSourceAsString());
        }
    }

    /**
     * 测试：分页查询
     */
    @Test
    public void test8() throws IOException {
        SearchRequest searchRequest = new SearchRequest("products");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("description","描述"))
                .from(0)    // 起始位置
                .size(2)     // 页大小
                .sort("price", SortOrder.DESC)  // 降序排序
                .fetchSource(new String[]{"title"}, new String[]{});    // 返回指定字段 -- 包含数组、不包含数组
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(searchResponse.status());
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    /**
     * 测试：高亮查询
     */
    @Test
    public void test9() throws IOException {
        SearchRequest searchRequest = new SearchRequest("products");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("description","描述"))
                .highlighter(new HighlightBuilder()
                        .requireFieldMatch(false)
                        .field("title")
                        .field("description")
                        .preTags("<span style ='color:red;'>")
                        .postTags("</span>"));  // 高亮
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        // 解析结果
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields.containsKey("description")) {
                System.out.println(highlightFields.get("description").fragments()[0]);
            }
            if (highlightFields.containsKey("title")) {
                System.out.println(highlightFields.get("title").fragments()[0]);
            }
            System.out.println("----->>> "+hit.getSourceAsString());
        }
    }

    /**
     * 测试：过滤查询
     */
    @Test
    public void test10() throws IOException {
        SearchRequest searchRequest = new SearchRequest("products");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery())
                .postFilter(QueryBuilders.idsQuery().addIds("1").addIds("2"));  // 指定过滤条件
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    /**
     * 测试：对象 -->> ES 存储
     */
    @Test
    public void test11() throws Exception {
        Product product = new Product();
        product.setId(1);
        product.setTitle("标题1111");
        product.setPrice(3.17);
        product.setDescription("描述11111");
        product.setCreated_at(new Date());
        // 录入 ES
        IndexRequest indexRequest = new IndexRequest("products");
        indexRequest.id(product.getId().toString())
                .source(new ObjectMapper().writeValueAsString(product),XContentType.JSON);
        IndexResponse indexResponse = elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse.status());
    }


    /**
     * 测试：聚合查询
     * @throws Exception
     */
    @Test
    public void test12() throws Exception{
        SearchRequest searchRequest = new SearchRequest("fruit");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder
                .query(QueryBuilders.matchAllQuery())
                .aggregation(AggregationBuilders.terms("price_group").field("price"))
                .size(0);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);

        // 处理聚合
        Aggregations aggregations = searchResponse.getAggregations();
        // 不同的 “桶” -- 对应不同的对象 Parse...
        ParsedDoubleTerms group = aggregations.get("price_group");
        List<? extends Terms.Bucket> buckets = group.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            System.out.println(bucket.getKey() + " --- " + bucket.getDocCount());
        }
    }

}
