package woohakdong.server.domain.ItemHistory;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubAccountHistory.ClubAccountHistory;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.item.ItemCategory;
import woohakdong.server.domain.member.Member;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ItemHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemHistoryId;

    private String itemReturnImage;

    @Column(nullable = false)
    private LocalDateTime itemRentalDate;

    private LocalDateTime itemReturnDate;
    private LocalDateTime itemDueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Builder
    public ItemHistory(String itemReturnImage, LocalDateTime itemRentalDate, LocalDateTime itemReturnDate,
                       LocalDateTime itemDueDate, Member member, Item item) {
        this.itemReturnImage = itemReturnImage;
        this.itemRentalDate = itemRentalDate;
        this.itemReturnDate = itemReturnDate;
        this.itemDueDate = itemDueDate;
        this.member = member;
        this.item = item;
    }
}
