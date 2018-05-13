package samsung.com.myplayer2.Class;

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemMoved(int fromPosition, int toPosition);

    void afterItemMoved();

    void onItemDismiss(int position);
}
