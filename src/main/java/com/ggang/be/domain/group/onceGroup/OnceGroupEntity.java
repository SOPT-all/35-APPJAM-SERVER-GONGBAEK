package com.ggang.be.domain.group.onceGroup;

import com.ggang.be.domain.BaseTimeEntity;
import com.ggang.be.domain.comment.CommentEntity;
import com.ggang.be.domain.constant.Category;
import com.ggang.be.domain.constant.Status;
import com.ggang.be.domain.gongbaekTimeSlot.GongbaekTimeSlotEntity;
import com.ggang.be.domain.user.UserEntity;
import com.ggang.be.domain.userOnceGroup.UserOnceGroupEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "once_group")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OnceGroupEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "once_group_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_user_id")
    private UserEntity userEntity;

    @OneToMany(mappedBy = "onceGroupEntity")
    private Set<UserOnceGroupEntity> participantUsers = new LinkedHashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "once_group_id")
    private List<CommentEntity> comments;

    @OneToOne
    @JoinColumn(name = "gongbaek_time_slot_id")
    private GongbaekTimeSlotEntity gongbaekTimeSlotEntity;

    @Column(nullable = false)
    private LocalDate groupDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    private int coverImg;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    private int maxPeopleCount;
    private int currentPeopleCount;

    @Column(nullable = false)
    private String introduction;

    @Column(nullable = false)
    private String title;


    @Builder
    private OnceGroupEntity(String title, String introduction, int currentPeopleCount,
        int maxPeopleCount, Status status, String location, int coverImg, Category category,
        LocalDate groupDate,LocalDate dueDate, List<CommentEntity> comments,
        UserEntity userEntity, GongbaekTimeSlotEntity gongbaekTimeSlotEntity) {
        this.title = title;
        this.introduction = introduction;
        this.currentPeopleCount = currentPeopleCount;
        this.maxPeopleCount = maxPeopleCount;
        this.status = status;
        this.location = location;
        this.coverImg = coverImg;
        this.category = category;
        this.gongbaekTimeSlotEntity = gongbaekTimeSlotEntity;

        this.groupDate = groupDate;
        this.dueDate = dueDate;
        this.comments = comments;
        this.userEntity = userEntity;
    }

    public boolean isHost(UserEntity currentUser) {
        return this.userEntity.getId().equals(currentUser.getId());
    }

    public boolean isApply(UserEntity currentUser) {
        return this.participantUsers.stream()
                .anyMatch(participant -> participant.getUserEntity().getId().equals(currentUser.getId()));
    }

    public void addComment(CommentEntity commentEntity) {
        this.comments.add(commentEntity);
    }
}
