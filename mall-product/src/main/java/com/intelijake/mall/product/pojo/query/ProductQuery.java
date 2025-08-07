package com.intelijake.mall.product.pojo.query;

/**
 * Product Query Object
 *
 * @author Jake
 * @since 2025-07-28
 */
public class ProductQuery {
    private Integer page;
    private Integer limit;
    private String name;
    private Integer categoryId;

    public ProductQuery() {}

    public ProductQuery(Integer page, Integer limit, String name, Integer categoryId) {
        this.page = page;
        this.limit = limit;
        this.name = name;
        this.categoryId = categoryId;
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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "ProductQuery{" +
                "page=" + page +
                ", limit=" + limit +
                ", name='" + name + '\'' +
                ", categoryId=" + categoryId +
                '}';
    }
}
