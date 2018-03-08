package com.dnastack.dos.registry.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * Models the page object in the search pagination implementation
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class Page {

    private Integer pageNumber;

    public Page() {
    }

    public Page(Integer pageNumber) {
        this.validatePageNumber(pageNumber);
        this.pageNumber = pageNumber;
    }

    private void validatePageNumber(Integer pageNumber) {
        Assert.isTrue(pageNumber.intValue() > 0,
                String.format("Page number can not be less than 1. Received page number: %d", new Object[]{pageNumber}));
    }

    public Integer getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.validatePageNumber(pageNumber);
        this.pageNumber = pageNumber;
    }

    @JsonIgnore
    public Page next() {
        return new Page(Integer.valueOf(this.pageNumber.intValue() + 1));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            Page that = (Page) obj;
            return Objects.equals(this.pageNumber, that.pageNumber);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.pageNumber});
    }

}
