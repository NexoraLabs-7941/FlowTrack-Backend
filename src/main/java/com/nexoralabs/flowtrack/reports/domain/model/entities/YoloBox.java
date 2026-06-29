package com.nexoralabs.flowtrack.reports.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class YoloBox {
    @Column(name = "box_top")
    private double top;
    
    @Column(name = "box_left")
    private double left;
    
    @Column(name = "box_width")
    private double width;
    
    @Column(name = "box_height")
    private double height;
}
