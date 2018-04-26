package samsung.com.myplayer2.Model;

/**
 * Created by 450G4 on 3/26/2018.
 */

public class Artist {
    private String name;
    private int albumCount;
    private int songCount;

    public Artist(String name, int albumCount, int songCount) {
        this.name = name;
        this.albumCount = albumCount;
        this.songCount = songCount;
    }

    public Artist(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAlbumCount() {
        return albumCount;
    }

    public void setAlbumCount(int albumCount) {
        this.albumCount = albumCount;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }
}
