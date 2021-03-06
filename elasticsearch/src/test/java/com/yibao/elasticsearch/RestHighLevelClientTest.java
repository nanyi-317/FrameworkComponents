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
    // ??????
    private final RestHighLevelClient elasticsearchClient;

    @Autowired
    public RestHighLevelClientTest(RestHighLevelClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    /**
     * ?????????????????????
     */
    @Test
    public void test2() throws IOException {
        AcknowledgedResponse products = elasticsearchClient.indices().delete(new DeleteIndexRequest("products"), RequestOptions.DEFAULT);
        System.out.println(products.isAcknowledged());
    }


    /**
     * ????????????????????? & ??????
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
        System.out.println(response.isAcknowledged());   // ??????????????????
        elasticsearchClient.close();    // ????????????

    }

    /**
     * ???????????????????????????
     */
    @Test
    public void test3() throws IOException {
        IndexRequest indexRequest = new IndexRequest("products");
        indexRequest.id("3")  // ???????????? id
                .source("{\n" +
                        "  \"id\":3,\n" +
                        "  \"title\":\"??????222222\",\n" +
                        "  \"price\":33333,\n" +
                        "  \"created_at\":\"2022-03-28\",\n" +
                        "  \"description\":\"??????333333\"\n" +
                        "}", XContentType.JSON);
        IndexResponse index = elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(index.status());
    }

    /**
     * ?????????????????????
     */
    @Test
    public void test4() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("products", "1");
        updateRequest.doc("{\"description\":\"??????\"}", XContentType.JSON);
        UpdateResponse update = elasticsearchClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update.status());
    }

    /**
     * ?????? ???????????????
     */
    @Test
    public void test5() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("products", "1");
        DeleteResponse delete = elasticsearchClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete.status());
    }


    /**
     * ??????????????? id ?????????????????????
     */
    @Test
    public void test6() throws IOException {
        GetRequest getRequest = new GetRequest("products", "1");
        GetResponse getResponse = elasticsearchClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString());
    }

    /**
     * ?????????????????????
     */
    @Test
    public void test7(QueryBuilder queryBuilder) throws IOException {
        // ??????????????????
        SearchRequest searchRequest = new SearchRequest("products");
        // ????????????
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(queryBuilder);
        // ??????????????????
        searchRequest.source(builder);
        // ??????
        SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        // ????????????
        System.out.println("????????? --- " + searchResponse.getHits().getTotalHits().value);
        System.out.println("???????????? --- " + searchResponse.getHits().getMaxScore());

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println("id -- " + hit.getId() + " -- ????????? -- " + hit.getSourceAsString());
        }
    }

    /**
     * ?????????????????????
     */
    @Test
    public void test8() throws IOException {
        SearchRequest searchRequest = new SearchRequest("products");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("description","??????"))
                .from(0)    // ????????????
                .size(2)     // ?????????
                .sort("price", SortOrder.DESC)  // ????????????
                .fetchSource(new String[]{"title"}, new String[]{});    // ?????????????????? -- ??????????????????????????????
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(searchResponse.status());
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    /**
     * ?????????????????????
     */
    @Test
    public void test9() throws IOException {
        SearchRequest searchRequest = new SearchRequest("products");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("description","??????"))
                .highlighter(new HighlightBuilder()
                        .requireFieldMatch(false)
                        .field("title")
                        .field("description")
                        .preTags("<span style ='color:red;'>")
                        .postTags("</span>"));  // ??????
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        // ????????????
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
     * ?????????????????????
     */
    @Test
    public void test10() throws IOException {
        SearchRequest searchRequest = new SearchRequest("products");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery())
                .postFilter(QueryBuilders.idsQuery().addIds("1").addIds("2"));  // ??????????????????
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    /**
     * ??????????????? -->> ES ??????
     */
    @Test
    public void test11() throws Exception {
        Product product = new Product();
        product.setId(1);
        product.setTitle("??????1111");
        product.setPrice(3.17);
        product.setDescription("??????11111");
        product.setCreated_at(new Date());
        // ?????? ES
        IndexRequest indexRequest = new IndexRequest("products");
        indexRequest.id(product.getId().toString())
                .source(new ObjectMapper().writeValueAsString(product),XContentType.JSON);
        IndexResponse indexResponse = elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse.status());
    }


    /**
     * ?????????????????????
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

        // ????????????
        Aggregations aggregations = searchResponse.getAggregations();
        // ????????? ????????? -- ????????????????????? Parse...
        ParsedDoubleTerms group = aggregations.get("price_group");
        List<? extends Terms.Bucket> buckets = group.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            System.out.println(bucket.getKey() + " --- " + bucket.getDocCount());
        }
    }

}
