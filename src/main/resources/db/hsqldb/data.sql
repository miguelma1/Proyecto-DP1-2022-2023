-- One admin user, named admin1 with password 4dm1n and authority admin
INSERT INTO users(username,password,enabled) VALUES ('admin1','4dm1n',TRUE);
INSERT INTO authorities(id,username,authority) VALUES (1,'admin1','admin');

INSERT INTO users(username,password,enabled) VALUES ('alvgonfri','1234',TRUE);
INSERT INTO authorities(id,username,authority) VALUES (4,'alvgonfri','player');
INSERT INTO users(username,password,enabled) VALUES ('davgonher1','ado',TRUE);
INSERT INTO authorities(id,username,authority) VALUES (5,'davgonher1','player');
INSERT INTO users(username,password,enabled) VALUES ('migmanalv','miguel1',TRUE);
INSERT INTO authorities(id,username,authority) VALUES (6,'migmanalv','player');
INSERT INTO users(username,password,enabled) VALUES ('player1','1234',TRUE);
INSERT INTO authorities(id,username,authority) VALUES (8,'player1','player');
INSERT INTO users(username,password,enabled) VALUES ('player2','1234',TRUE);
INSERT INTO authorities(id,username,authority) VALUES (9,'player2','player');
INSERT INTO users(username,password,enabled) VALUES ('player3','1234',TRUE);
INSERT INTO authorities(id,username,authority) VALUES (10,'player3','player');
INSERT INTO users(username,password,enabled) VALUES ('player4','1234',TRUE);
INSERT INTO authorities(id,username,authority) VALUES (11,'player4','player');
INSERT INTO users(username,password,enabled) VALUES ('player5','1234',TRUE);
INSERT INTO authorities(id,username,authority) VALUES (12,'player5','player');
INSERT INTO users(username,password,enabled) VALUES ('player6','1234',TRUE);
INSERT INTO authorities(id,username,authority) VALUES (13,'player6','player');
INSERT INTO users(username,password,enabled) VALUES ('player7','1234',TRUE);
INSERT INTO authorities(id,username,authority) VALUES (14,'player7','player');
INSERT INTO users(username,password,enabled) VALUES ('player8','1234',TRUE);
INSERT INTO authorities(id,username,authority) VALUES (15,'player8','player');
INSERT INTO users(username,password,enabled) VALUES ('player9','1234',TRUE);
INSERT INTO authorities(id,username,authority) VALUES (16,'player9','player');

INSERT INTO players(id,online,playing,username) VALUES
(1, FALSE, FALSE , 'alvgonfri'),
(2, FALSE, FALSE , 'davgonher1'),
(3, FALSE, FALSE, 'migmanalv'),
(5, FALSE, FALSE , 'player1'),
(6, FALSE, FALSE , 'player2'),
(7, FALSE, FALSE , 'player3'),
(8, FALSE, FALSE , 'player4'),
(9, FALSE, FALSE , 'player5'),
(10, FALSE, FALSE , 'player6'),
(11, FALSE, FALSE , 'player7'),
(12, FALSE, FALSE , 'player8'),
(13, FALSE, FALSE , 'player9');

INSERT INTO suffragium_cards(id,loyals_votes,traitors_votes,vote_limit) VALUES
(1,0,0,13),
(2,0,0,15),
(3,8,10,13),
(4,9,6,15),
(5,0,0,17),
(6,7,5,13);

INSERT INTO turns(id,current_turn) VALUES 
(1, 1),
(2, 5),
(3, 1),
(4, 1),
(5, 1),
(6, 2);

INSERT INTO games(id,name,public_game,state,num_players,start_date,end_date,round,turn_id,stage,winners,suffragium_card_id) VALUES
(1,'Mi primera partida', 0, 'STARTING', 5, null, null, 'FIRST', 1, 'VOTING', null, 1),
(2,'Partida rapida', 1, 'STARTING', 6, '2022-10-27 10:00:00', null, 'FIRST', 2, 'END_OF_TURN', null, 2),
(3,'Partida de principiantes', 0, 'FINISHED', 5, '2022-10-30 10:00:00', '2022-10-30 11:00:00', 'FIRST', 3, 'VOTING', 'TRAITORS', 3),
(4,'New game', 1, 'FINISHED', 6, '2022-11-15 23:59:58', '2022-11-16 00:25:01', 'FIRST', 4, 'VOTING', 'LOYALS', 4),
(5,'Testing decks', 1, 'STARTING', 7, null, null, 'FIRST', 5, 'VOTING', null, null),
(6,'Strategic game', 0, 'IN_PROCESS', 5, '2023-01-11 18:24:49', null, 'SECOND', 6, 'VETO', null, null);


INSERT INTO player_infos(id,creator,spectator,game_id,player_id) VALUES 
(1,true,false,2,1),
(2,false,false,2,2),
(3,false,false,2,3),
(5,false,false,2,5),
(6,false,true,2,6),
(7,false,false,2,7),

(8,false,false,1,3),

(30,false,false,3,1),
(31,false,false,3,5),
(32,false,false,3,8),
(33,false,false,3,10),
(34,false,false,3,11),

(40,true,false,4,1),
(41,false,false,4,5),
(42,false,false,4,6),
(43,false,false,4,7),
(44,false,false,4,8),
(45,false,false,4,9),

(50,true,false,5,1),
(51,false,false,5,2),
(52,false,false,5,3),
(53,false,false,5,5),
(54,false,false,5,6),
(55,false,false,5,7),
(56,false,false,5,8),

(60,false,false,6,1);

INSERT INTO achievements(id,name,type,description,threshold) VALUES 
(1,'Casual player','GAMES','You have played <THRESHOLD> games.',10.0),
(2,'Advanced player','GAMES','You have played <THRESHOLD> games.',50.0),
(3,'Addicted player','GAMES','You have played <THRESHOLD> games.',100.00),
(4,'Understanding the rules','VICTORY','You have won <THRESHOLD> games.',5.0),
(5,'You are improving!','VICTORY','You have won <THRESHOLD> games.',25.0),
(6,'Invincible','VICTORY','You have won <THRESHOLD> games.',50.00),
(7,'A good loyal','VICTORY','You have won <THRESHOLD> games as loyal.',10.00),
(8,'The best loyal','VICTORY','You have won <THRESHOLD> games as loyal.',20.0),
(9,'A good merchant','VICTORY','You have won <THRESHOLD> games as merchant.',10.0),
(10,'The best merchant','VICTORY','You have won <THRESHOLD> games as merchant.',20.00),
(11,'A good traitor','VICTORY','You have won <THRESHOLD> games as traitor.',10.0),
(12,'The best traitor','VICTORY','You have won <THRESHOLD> games as traitor.',20.0),
(13,'Meeting new players','FRIENDS','You have <THRESHOLD> friends.',1.00),
(14,'Your new family','FRIENDS','You have <THRESHOLD> friends.',10.00),
(15,'Networking king','FRIENDS','You have <THRESHOLD> friends.',50.00),
(16,'Trying the game','TIME','You have played <THRESHOLD> minutes.',10.00),
(17,'You love the game','TIME','You have played <THRESHOLD> minutes.',50.00),
(18,'You are hooked!','TIME','You have played <THRESHOLD> minutes.',200.00);

INSERT INTO faction_cards(type) VALUES 
('LOYAL'), ('TRAITOR'), ('MERCHANT');

INSERT INTO vote_cards(type) VALUES 
('GREEN'), ('RED'), ('YELLOW');

INSERT INTO decks(id, role_cards,player_id,game_id) VALUES 
(1, 'CONSUL',1,2),
(2, 'PRETOR',5,2),
(3, 'EDIL',2,2),
(4, 'EDIL',3,2),
(5, 'NO_ROL',6,2),
(6, 'NO_ROL',7,2),
(7, 'EDIL', 3, 1),

(30, 'NO_ROL', 1, 3),
(31, 'CONSUL', 5, 3),
(32, 'PRETOR', 8, 3),
(33, 'EDIL', 10, 3),
(34, 'EDIL', 11, 3),

(40, 'NO_ROL', 1, 4),
(41, 'CONSUL', 5, 4),
(42, 'PRETOR', 6, 4),
(43, 'NO_ROL', 7, 4),
(44, 'EDIL', 8, 4),
(45, 'EDIL', 9, 4);

INSERT INTO decks_faction_cards(deck_id, faction_cards_type) VALUES 
(1, 'LOYAL'),
(2,'TRAITOR'),
(3,'MERCHANT'),
(4,'MERCHANT'),
(5,'TRAITOR'),
(6,'LOYAL'),

(30,'TRAITOR'),
(31,'LOYAL'),
(32,'MERCHANT'),
(33,'TRAITOR'),
(34,'LOYAL'),

(40,'LOYAL'),
(41,'LOYAL'),
(42,'MERCHANT'),
(43,'TRAITOR'),
(44,'LOYAL'),
(45,'TRAITOR');

INSERT INTO decks_vote_cards(deck_id, vote_cards_type) VALUES 
(3, 'YELLOW'),
(3, 'RED'),
(4,'GREEN'),
(4,'RED');

INSERT INTO invitations(invitation_type,message,accepted,sender_id,recipient_id,game_id) VALUES
('FRIENDSHIP', 'Hi, could we be friends?', FALSE, 1, 3, null),
('FRIENDSHIP', 'Hi, could we start a friendship?', FALSE, 2, 1, null),
('FRIENDSHIP', 'I am player1', FALSE, 5, 1, null),
('FRIENDSHIP', 'I am player2', TRUE, 6, 1, null),
('FRIENDSHIP', 'I am alvgonfri', TRUE, 1, 9, null),
('GAME_PLAYER', 'Join my game!', FALSE, 1, 9, 5);

INSERT INTO comments(id,message,date,player_info_id) VALUES
(1,'GG', '2022-10-27 10:01:00',1),
(2,'GG', '2022-10-27 10:02:00',2),
(3,'GG', '2022-10-27 10:03:00',3),
(4,'GG', '2022-10-27 10:04:00',5);

