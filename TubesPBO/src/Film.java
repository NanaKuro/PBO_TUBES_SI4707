public class Film {
    String judulFilm;
    String genreFilm;
    Integer tahunRilis;
    

    public Film(String judulFilm, String genreFilm, int tahunRilis) {
        this.judulFilm = judulFilm;
        this.genreFilm = genreFilm;
        this.tahunRilis = tahunRilis;
    }

    public String getJudulFilm() {
        return judulFilm;
    }

    public void setJudulFilm(String judulFilm) {
        this.judulFilm = judulFilm;
    }

    public String getGenreFilm() {
        return genreFilm;
    }

    public void setGenreFilm(String genreFilm) {
        this.genreFilm = genreFilm;
    }

    public Integer getTahunRilis() {
        return tahunRilis;
    }

    public void setTahunRilis(Integer tahunRilis) {
        this.tahunRilis = tahunRilis;
    }

    public int getId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getId'");
    }
    
}
