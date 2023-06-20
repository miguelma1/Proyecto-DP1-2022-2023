package org.springframework.samples.petclinic.comment;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.playerInfo.PlayerInfo;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "comments")
public class Comment extends BaseEntity{

    @NotBlank
	private String message;
    
    private Date date;

    @ManyToOne
    private PlayerInfo playerInfo;
}
