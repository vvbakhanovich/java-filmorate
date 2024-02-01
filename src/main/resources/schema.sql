DROP TABLE IF EXISTS GENRE, MPA, FILM, FILM_GENRE, FILMORATE_USER, FRIENDSHIP_STATUS, FRIENDSHIP, FILM_LIKE, REVIEW, REVIEW_LIKE, DIRECTOR, FILM_DIRECTOR;

CREATE TABLE IF NOT EXISTS GENRE(
    ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,
    GENRE_NAME CHARACTER VARYING(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS MPA(
    ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,
    RATING_NAME CHARACTER VARYING(5) NOT NULL
);

CREATE TABLE IF NOT EXISTS FILM(
    ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,
    TITLE CHARACTER VARYING(255) NOT NULL,
    DESCRIPTION CHARACTER VARYING(200),
    RELEASE_DATE DATE NOT NULL,
    DURATION INTEGER NOT NULL,
    MPA_ID INTEGER NOT NULL,
	FOREIGN KEY (MPA_ID) REFERENCES MPA(ID)
);


CREATE TABLE IF NOT EXISTS FILM_GENRE(
    ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,
    FILM_ID INTEGER NOT NULL,
    GENRE_ID INTEGER NOT NULL,
	FOREIGN KEY (FILM_ID) REFERENCES FILM(ID) ON DELETE CASCADE,
	FOREIGN KEY (GENRE_ID) REFERENCES GENRE(ID)
);

CREATE UNIQUE INDEX FILM_GENRE_IDX ON FILM_GENRE (ID, FILM_ID, GENRE_ID);

CREATE TABLE IF NOT EXISTS FILMORATE_USER(
    ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,
    EMAIL CHARACTER VARYING(255) NOT NULL,
    LOGIN CHARACTER VARYING(255) NOT NULL,
    NICKNAME CHARACTER VARYING(255) NOT NULL,
    BIRTHDAY DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS FRIENDSHIP_STATUS(
    ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,
    STATUS_NAME CHARACTER VARYING(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS FRIENDSHIP(
    USER_ID INTEGER NOT NULL,
    FRIEND_ID INTEGER NOT NULL,
    FRIENDSHIP_STATUS_ID INTEGER NOT NULL,
	CONSTRAINT pk_friendship PRIMARY KEY (USER_ID, FRIEND_ID),
	FOREIGN KEY (USER_ID) REFERENCES FILMORATE_USER(ID) ON DELETE CASCADE,
	FOREIGN KEY (FRIEND_ID) REFERENCES FILMORATE_USER(ID) ON DELETE CASCADE,
	FOREIGN KEY (FRIENDSHIP_STATUS_ID) REFERENCES FRIENDSHIP_STATUS(ID)
);


CREATE TABLE IF NOT EXISTS FILM_LIKE(
    FILM_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
	CONSTRAINT pk_film_like PRIMARY KEY (FILM_ID, USER_ID),
	FOREIGN KEY (FILM_ID) REFERENCES FILM(ID) ON DELETE CASCADE,
	FOREIGN KEY (USER_ID) REFERENCES FILMORATE_USER(ID)
);

CREATE TABLE IF NOT EXISTS REVIEW(
    ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,
    REVIEW_CONTENT CHARACTER VARYING(255) NOT NULL,
    IS_POSITIVE BOOLEAN NOT NULL,
    USEFUL INTEGER,
    USER_ID INTEGER NOT NULL,
    FILM_ID INTEGER NOT NULL,
    FOREIGN KEY (USER_ID) REFERENCES FILMORATE_USER(ID) ON DELETE CASCADE,
    FOREIGN KEY (FILM_ID) REFERENCES FILM(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REVIEW_LIKE(
    REVIEW_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
    LIKE_TYPE CHARACTER VARYING(7) NOT NULL,
    FOREIGN KEY (REVIEW_ID) REFERENCES REVIEW(ID) ON DELETE CASCADE,
    FOREIGN KEY (USER_ID) REFERENCES FILMORATE_USER(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS DIRECTOR(
    ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,
    DIRECTOR_NAME CHARACTER VARYING(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS FILM_DIRECTOR(
    ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,
    FILM_ID INTEGER NOT NULL,
    DIRECTOR_ID INTEGER NOT NULL,
	FOREIGN KEY (FILM_ID) REFERENCES FILM(ID) ON DELETE CASCADE,
	FOREIGN KEY (DIRECTOR_ID) REFERENCES DIRECTOR(ID) ON DELETE CASCADE
);

CREATE UNIQUE INDEX FILM_DIRECTOR_IDX ON FILM_DIRECTOR (ID, FILM_ID, DIRECTOR_ID);

