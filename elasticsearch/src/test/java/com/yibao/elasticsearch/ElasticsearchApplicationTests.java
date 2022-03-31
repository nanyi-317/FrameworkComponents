package com.yibao.elasticsearch;

import com.yibao.elasticsearch.entity.Product;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;

import java.io.IOException;

@SpringBootTest
class ElasticsearchApplicationTests{

    // 对象
    private final ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public ElasticsearchApplicationTests(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    /**
     * 测试：创建索引 & 添加文档
     */
    @Test
    public void test1() {
        Product product = new Product();
        product.setId(1);
        product.setTitle("这是第一个文档");
        product.setPrice(1.11);
        product.setDescription("这个第一个文档的描述信息");
        elasticsearchOperations.save(product);
    }

    /**
     * 测试：更新文档
     */
    @Test
    public void test2() {
        Product product = new Product();
        product.setId(1);
        product.setTitle("这是第一个文档");
        product.setPrice(3.17);
        product.setDescription("这个第一个文档的描述信息");
        elasticsearchOperations.save(product);

        Product product2 = new Product();
        product2.setId(2);
        product2.setTitle("这是第2个文档");
        product2.setPrice(2.22);
        product2.setDescription("这个第2个文档的描述信息");
        elasticsearchOperations.save(product2);
    }

    /**
     * 测试：查询一条文档
     */
    @Test
    public void test3() {
        Product product = elasticsearchOperations.get("1", Product.class);
        System.out.println(product);
    }

    /**
     * 测试：删除文档
     */
    @Test
    public void test4() {
        elasticsearchOperations.delete("1", Product.class);
    }

    /**
     * 测试：删除所有
     */
    @Test
    public void test5() {
        elasticsearchOperations.delete(Query.findAll(), Product.class);
    }

    /**
     * 测试：查询所有
     */
    @Test
    public void test6() {
        SearchHits<Product> search = elasticsearchOperations.search(Query.findAll(), Product.class);
        for (SearchHit<Product> productSearchHit : search) {
            System.out.println(productSearchHit.getContent());
        }
    }

}
