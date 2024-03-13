package com.elixr.hinachos.server.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class HiNachosTagsSearchCriteria {
    private String tagId;
    private Timestamp startDate;
    private Timestamp endDate;
    private Long pageNumber;
}
