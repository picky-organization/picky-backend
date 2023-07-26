package network.picky.web.auth.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import network.picky.web.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor
public class SavedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    @Column(unique = true,nullable = false)
    private String refreshToken;

    public SavedToken(Member member, String refreshToken) {
        this.member = member;
        this.refreshToken = refreshToken;
    }
}