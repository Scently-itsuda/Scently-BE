package com.itsuda.perfume.domain;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class Notification extends BaseEntity {
    private String title;

    private String bodyMessage;
}
