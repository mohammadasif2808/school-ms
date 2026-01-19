package com.school.academic.frontoffice.dto.enquiry;

import com.school.academic.frontoffice.dto.PageMetadata;

import java.util.List;

/**
 * Paginated response for admission enquiries.
 */
public class AdmissionEnquiryPageResponse {

    private List<AdmissionEnquiryResponse> content;
    private PageMetadata page;

    public AdmissionEnquiryPageResponse() {
    }

    public AdmissionEnquiryPageResponse(List<AdmissionEnquiryResponse> content, PageMetadata page) {
        this.content = content;
        this.page = page;
    }

    // Getters and Setters
    public List<AdmissionEnquiryResponse> getContent() {
        return content;
    }

    public void setContent(List<AdmissionEnquiryResponse> content) {
        this.content = content;
    }

    public PageMetadata getPage() {
        return page;
    }

    public void setPage(PageMetadata page) {
        this.page = page;
    }
}
