CREATE TABLE IF NOT EXISTS film_data  (
          id  INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
          name  TEXT,
          state  TEXT,
          leader  TEXT,
          actor  TEXT,
          img_url  TEXT,
          agree  INTEGER,
          danmu TEXT
);

CREATE TABLE IF NOT EXISTS week  (
                                     id  INTEGER,
                                     week  INTEGER
);

CREATE TABLE IF NOT EXISTS userlib  (
                                        user  TEXT,
                                        pawd  TEXT,
                                        cookie  TEXT,
                                        name  TEXT,
                                        icon  TEXT,
                                        setting  TEXT,
                                        vip int
);
CREATE TABLE IF NOT EXISTS banner (
                                      type  TEXT,
                                      img  TEXT,
                                      url  TEXT,
                                      id  int,
                                      title  TEXT,
                                      bid INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT
);
CREATE TABLE IF NOT EXISTS  fast_functions(
                                      title  TEXT,
                                      href  TEXT,
                                      img  TEXT,
                                      fid INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT
);
CREATE TABLE IF NOT EXISTS badmsg  (
                  id  INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
                  user  TEXT,
                  time  TEXT,
                  banuser  TEXT,
                  content  TEXT,
                  type  int,
                  filmid  int,
                  danmuid  int,
                  part TEXT
);