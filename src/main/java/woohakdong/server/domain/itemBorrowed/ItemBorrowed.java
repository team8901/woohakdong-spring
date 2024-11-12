package woohakdong.server.domain.itemBorrowed;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.item.Item;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ItemBorrowed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemBorrowedId;

    private LocalDateTime itemBorrowedReturnDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_member_id")
    private ClubMember clubMember;

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Builder
    private ItemBorrowed(ClubMember clubMember, Item item, LocalDateTime itemBorrowedReturnDate) {
        this.clubMember = clubMember;
        this.item = item;
        this.itemBorrowedReturnDate = itemBorrowedReturnDate;
    }

    public static ItemBorrowed createItemBorrowed(ClubMember clubMember, Item item,
                                                  LocalDateTime itemBorrowedReturnDate) {
        return ItemBorrowed.builder()
                .itemBorrowedReturnDate(itemBorrowedReturnDate)
                .clubMember(clubMember)
                .item(item)
                .build();
    }
}
