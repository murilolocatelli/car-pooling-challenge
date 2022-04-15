package com.cabify.carpooling.model;

import com.cabify.carpooling.enumeration.JourneyStatus;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "journey")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "registeredJourney")
@EqualsAndHashCode(of = {"id"})
@Builder
public class Journey implements Serializable {

    private static final long serialVersionUID = 518969241357390291L;

    @NotNull
    @Id
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Integer people;

    @Column(nullable = false)
    private JourneyStatus status;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @OneToOne(mappedBy = "journey")
    private RegisteredJourney registeredJourney;

    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now();
    }

}
