package samsung.com.myplayer2.Handler;

import java.util.ArrayList;

import samsung.com.myplayer2.Model.Song;

public class MusicStore {

    public MusicStore() {
    }

    //song list of all
    private ArrayList<Song> allsongs;
    //List from Album
    private ArrayList<Song> SongListOfAlbum;
    //List from Artist
    private ArrayList<Song> SongListOfArtist;
    //List from Playlist
    private ArrayList<Song> SongListOfPlaylist;
    //List from Genres
    private ArrayList<Song> SongListOfGenres;
    //List from Search
    private ArrayList<Song> SongListOfSearch;


    
    public ArrayList<Song> getAllsongs() {
        return allsongs;
    }

    public void setAllsongs(ArrayList<Song> allsongs) {
        this.allsongs = allsongs;
    }

    public ArrayList<Song> getSongListOfAlbum() {
        return SongListOfAlbum;
    }

    public void setSongListOfAlbum(ArrayList<Song> songListOfAlbum) {
        SongListOfAlbum = songListOfAlbum;
    }

    public ArrayList<Song> getSongListOfArtist() {
        return SongListOfArtist;
    }

    public void setSongListOfArtist(ArrayList<Song> songListOfArtist) {
        SongListOfArtist = songListOfArtist;
    }

    public ArrayList<Song> getSongListOfPlaylist() {
        return SongListOfPlaylist;
    }

    public void setSongListOfPlaylist(ArrayList<Song> songListOfPlaylist) {
        SongListOfPlaylist = songListOfPlaylist;
    }

    public ArrayList<Song> getSongListOfGenres() {
        return SongListOfGenres;
    }

    public void setSongListOfGenres(ArrayList<Song> songListOfGenres) {
        SongListOfGenres = songListOfGenres;
    }

    public ArrayList<Song> getSongListOfSearch() {
        return SongListOfSearch;
    }

    public void setSongListOfSearch(ArrayList<Song> songListOfSearch) {
        SongListOfSearch = songListOfSearch;
    }
}
