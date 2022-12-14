package com.markaz.pillar.transaction.repository.model;

import com.markaz.pillar.auth.repository.models.AuthUser;
import com.markaz.pillar.donation.repository.model.DonationDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "user_donation",
        indexes = {
                @Index(columnList = "trx_id", unique = true)
        }
)
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class UserTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "trx_id")
    @NotBlank
    private String trxId;

    @ManyToOne(
            targetEntity = DonationDetail.class,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
    )
    @JoinColumn(name = "donation_id", nullable = false)
    private DonationDetail donationDetail;

    @ManyToOne(
            targetEntity = AuthUser.class,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
    )
    @JoinColumn(name = "user_id", nullable = false)
    private AuthUser user;

    @NotBlank
    @URL
    @Size(max = 2048)
    @Column(name = "donation_url")
    private String donationURL;

    @NotNull
    private Long amount;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TransactionStatus status = TransactionStatus.MENUNGGU_KONFIRMASI;

    @Size(max = 2048)
    private String reason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;
}
