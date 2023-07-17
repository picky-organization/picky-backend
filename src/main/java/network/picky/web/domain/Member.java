package network.picky.web.domain;

import org.springframework.boot.autoconfigure.domain.EntityScan;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationaType.AUTO)

}
