package com.school.academic.frontoffice.dto.complaint;

import com.school.academic.frontoffice.dto.PageMetadata;

import java.util.List;

/**
 * Paginated response for complaints.
 */
public class ComplaintPageResponse {

    private List<ComplaintResponse> content;
    private PageMetadata page;

    public ComplaintPageResponse() {
    }

    public ComplaintPageResponse(List<ComplaintResponse> content, PageMetadata page) {
        this.content = content;
        this.page = page;
    }

    // Getters and Setters
    public List<ComplaintResponse> getContent() {
        return content;
    }

    public void setContent(List<ComplaintResponse> content) {
        this.content = content;
    }

    public PageMetadata getPage() {
        return page;
    }

    public void setPage(PageMetadata page) {
        this.page = page;
    }
}
