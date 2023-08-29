package com.lw.fx.client.domain;

import lombok.Data;

@Data
public class ActiveProjects {

    private String projectName;
    private String projectLead;
    private String assignee;
    private String status;
    private Double progress;
    private String dueDate;
}
