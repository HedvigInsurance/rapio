package com.hedvig.rapio.external

import com.hedvig.rapio.apikeys.Partner
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
class ExternalMember(
    @Id
    val id: UUID,
    @Column(unique = true)
    val memberId: String,
    @Enumerated(EnumType.STRING)
    val partner: Partner
) {
    @field:CreationTimestamp
    lateinit var createdAt: Instant
}