package com.intelijake.mall.product.pojo.query;

/**
 * Product Category Query Object
 *
 * @author Jake
 * @since 2025-07-28
 */
public class ProductCategoryQuery {
    private Integer page;
    private Integer limit;
    private String name;

    public ProductCategoryQuery() {}

    public ProductCategoryQuery(Integer page, Integer limit, String name) {
        this.page = page;
        this.limit = limit;
        this.name = name;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ProductCategoryQuery{" +
                "page=" + page +
                ", limit=" + limit +
                ", name='" + name + '\'' +
                '}';
    }
}
