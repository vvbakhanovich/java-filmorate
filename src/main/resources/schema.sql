
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
    FILM_ID INTEGER NOT NULL,
    GENRE_ID INTEGER NOT NULL,
	CONSTRAINT pk_film_genre PRIMARY KEY (FILM_ID, GENRE_ID),
	FOREIGN KEY (FILM_ID) REFERENCES FILM(ID),
	FOREIGN KEY (GENRE_ID) REFERENCES GENRE(ID)
);

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
	FOREIGN KEY (FILM_ID) REFERENCES FILM(ID),
	FOREIGN KEY (USER_ID) REFERENCES FILMORATE_USER(ID)
);