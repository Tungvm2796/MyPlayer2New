package samsung.com.myplayer2.Model;

/**
 * Created by 450G4 on 3/17/2018.
 */

public class Playlist {
    long listid;
    String name;
    int songCount;

    public Playlist() {
    }

    public Playlist(long listid, String name, int count) {
        this.listid = listid;
        this.name = name;
        this.songCount = count;
    }

    public Long getListid() {
        return listid;
    }

    public void setListid(Long listid) {
        this.listid = listid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

}
