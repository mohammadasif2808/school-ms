package com.school.academic.frontoffice.dto.visitor;

import com.school.academic.frontoffice.dto.PageMetadata;

import java.util.List;

/**
 * Paginated response for visitors.
 */
public class VisitorPageResponse {

    private List<VisitorResponse> content;
    private PageMetadata page;

    public VisitorPageResponse() {
    }

    public VisitorPageResponse(List<VisitorResponse> content, PageMetadata page) {
        this.content = content;
        this.page = page;
    }

    // Getters and Setters
    public List<VisitorResponse> getContent() {
        return content;
    }

    public void setContent(List<VisitorResponse> content) {
        this.content = content;
    }

    public PageMetadata getPage() {
        return page;
    }

    public void setPage(PageMetadata page) {
        this.page = page;
    }
}
