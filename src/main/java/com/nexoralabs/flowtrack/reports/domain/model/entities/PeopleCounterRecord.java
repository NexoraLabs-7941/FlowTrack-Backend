package com.nexoralabs.flowtrack.reports.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "people_counter_records")
@Getter
@Setter
public class PeopleCounterRecord {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "camera_id")
    private String cameraId;

    @Column(name = "record_date")
    private String date;

    @Column(name = "record_hour")
    private String hour;

    @Column(name = "entries")
    private int entries;

    @Column(name = "exits")
    private int exits;

    @Column(name = "current_inside")
    private int currentInside;
}
