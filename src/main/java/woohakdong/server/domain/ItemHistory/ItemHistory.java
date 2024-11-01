package woohakdong.server.domain.ItemHistory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.member.Member;

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

    public void setItemReturnImage(String itemReturnImage) {
        this.itemReturnImage = itemReturnImage;
    }

    public void setItemReturnDate(LocalDateTime itemReturnDate) {
        this.itemReturnDate = itemReturnDate;
    }
}
