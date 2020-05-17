package com.myeotra.user.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RateCommentResponse {

    @SerializedName("rate_comment")
    @Expose
    private List<RateComment> rateComment = null;

    public List<RateComment> getRateComment() {
        return rateComment;
    }

    public void setRateComment(List<RateComment> rateComment) {
        this.rateComment = rateComment;
    }

    public class RateComment {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("comment")
        @Expose
        private String comment;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

    }
}
