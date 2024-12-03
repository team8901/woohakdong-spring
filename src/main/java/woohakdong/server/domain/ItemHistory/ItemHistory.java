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
import woohakdong.server.domain.BaseEntity;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.member.Member;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ItemHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemHistoryId;

    private String itemReturnImage;

    @Column(nullable = false)
    private LocalDateTime itemRentalDate;

    private LocalDateTime itemReturnDate;
    private LocalDateTime itemDueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_member_id")
    private ClubMember clubMember;

    private String memberName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @Builder
    private ItemHistory(String itemReturnImage, LocalDateTime itemRentalDate, LocalDateTime itemReturnDate,
                        LocalDateTime itemDueDate, ClubMember clubMember, Item item, String memberName, Club club) {
        this.itemReturnImage = itemReturnImage;
        this.itemRentalDate = itemRentalDate;
        this.itemReturnDate = itemReturnDate;
        this.itemDueDate = itemDueDate;
        this.clubMember = clubMember;
        this.item = item;
        this.memberName = memberName;
        this.club = club;
    }

    public static ItemHistory create(ClubMember clubMember, String memberName, Item item, LocalDateTime itemRentalDate,
                                     LocalDateTime itemDueDate, Club club) {
        return ItemHistory.builder()
                .itemReturnImage(null)
                .itemRentalDate(itemRentalDate)
                .itemReturnDate(null)
                .itemDueDate(itemDueDate)
                .clubMember(clubMember)
                .item(item)
                .memberName(memberName)
                .club(club)
                .build();

    }

    public void setItemReturnImage(String itemReturnImage) {
        this.itemReturnImage = itemReturnImage;
    }

    public void setItemReturnDate(LocalDateTime itemReturnDate) {
        this.itemReturnDate = itemReturnDate;
    }
}
