INSERT INTO User (typeOfUser,username,hashedPassword,firstName,infix,lastName,coinBalance,phoneNumber,emailaddress) VALUES
                                                                                                                        ('Huurder','t01','t01','t01firstName','','t01lastName',0,'0600000001','t01@fake.com'),
                                                                                                                        ('Huurder','t02','t02','t02firstName','','t02lastName',10,'0600000002','t02@fake.com'),
                                                                                                                        ('Huurder','t03','t03','t03firstName','','t03lastName',800,'0600000003','t03@fake.com'),
                                                                                                                        ('Huurder','t04','t04','t04firstName','','t04lastName',2500,'0600000004','t04@fake.com'),
                                                                                                                        ('Huurder','t05','t05','t05firstName','','t05lastName',30000,'0600000005','t05@fake.com'),
                                                                                                                        ('Verhuurder','t06','t06','t06firstName','','t06lastName',0,'0600000006','t06@fake.com'),
                                                                                                                        ('Verhuurder','t07','t07','t07firstName','','t07lastName',10,'0600000007','t07@fake.com'),
                                                                                                                        ('Verhuurder','t08','t08','t08firstName','','t08lastName',800,'0600000008','t08@fake.com'),
                                                                                                                        ('Verhuurder','t09','t09','t09firstName','','t09lastName',2500,'0600000009','t09@fake.com'),
                                                                                                                        ('Verhuurder','t10','t10','t10firstName','','t10lastName',30000,'0600000010','t10@fake.com');



INSERT INTO Message (senderId,receiverId,dateTimeSent,subject,body,archivedBySender,archivedByReceiver,readByReceiver) VALUES
                                                                                                                                (1,2,'2024-01-19 03:14:07','He','blabla','0','0','1'),
                                                                                                                            (1,2,'2024-01-20 03:14:07','Hoi','blablabla','0','0','0'),
                                                                                                                            (2,1,'2024-01-21 03:14:07','hallo','blablablablablabla','0','0','1'),
                                                                                                                            (2,1,'2024-01-22 03:14:07','he ha','blablablablablabla','0','0','0'),
                                                                                                                            (9,7,'2024-01-23 03:14:07','Bonjour','blablabla','0','0','1'),
                                                                                                                            (9,8,'2024-01-24 03:14:07','Ola','blablablablablabla','0','0','1'),
                                                                                                                            (9,10,'2024-01-25 03:14:07','Nihon','blablabla','0','0','1'),
                                                                                                                            (10,9,'2024-01-26 03:14:07','Gruetzi','blablabla','0','0','1'),
                                                                                                                            (10,9,'2024-01-27 03:14:07','Doei','bla','0','0','1');


INSERT INTO BlockedList (userId,blockedUser) VALUES
                                                    (6,1),
                                                    (8,2),
                                                    (7,3);



INSERT INTO ExtraFeature (extraFeatureId,extraFeatureName) VALUES
                                                               (1,'Wifi'),
                                                               (2,'Keuken'),
                                                               (3,'Wasmachine'),
                                                               (4,'TV'),
                                                               (5,'Droger'),
                                                               (6,'Airconditioning'),
                                                               (7,'Verwarming'),
                                                               (8,'Zwembad'),
                                                               (9,'Gratis parkeren'),
                                                               (10,'Ontbijt');

INSERT INTO House (houseId,houseName,houseTypeId,houseOwnerId,province,city,streetAndNumber,zipcode,maxGuest,roomCount,pricePPPD,description,isNotAvailable) VALUES

                                                                                                                                                                 (1, 'Het Boshuisje. Voel je thuis in dit huis en geniet', 1, 6, 'Overijssel', 'Oldenzaal', 'Grondmanstraat 10', '7572PB', 2, 1, 55, 'Even tot rust komen? Alleen of met zijn tweeën? Of welverdiende quality-time met zijn tweeën? Dit is de perfecte plek om aan de drukte van de stad te ontsnappen, te schrijven, muziek componeren, mediteren of om even heerlijk te relaxen.', 0),
                                                                                                                                                                 (2, 'De Roode Stee Grolloo (eigen ingang)', 2, 7, 'Drenthe', 'Grolloo', 'Middenstreek 5', '9444PD', 4, 2, 60, 'Onze B&B biedt u een ruim appartement(45m2), afsluitbaar, op de 1e verdieping met eigen ingang. Hierdoor is contactloos verblijven mogelijk.', 1),
                                                                                                                                                                 (3, 'Chalet Musselkanaal', 3, 8, 'Groningen', 'Musselkanaal', 'Sluisstraat 26', '9581JB', 6, 3, 100, 'Chalet Musa heeft een heerlijke tropische tuin die volledig is omheind. Hierdoor zit u helemaal privé. Er is een prachtige veranda tot uw beschikking met uitzicht over de landerijen. Verder is het chalet helemaal nieuw en van alle gemakken voorzien, waardoor u kunt genieten van een zorgeloos verblijf.', 0),
                                                                                                                                                                 (4, 'Standard Classic Room - Park Centraal Den Haag', 4, 9, 'Zuid-Holland', 'Den Haag', 'Deltaplein 200', '2554EJ', 2, 1, 80, 'De Classic Room biedt 16 m² ruimte. Het beschikt over een tweepersoonsbed, kingsize of queensize bed, een aparte badkamer met een bad en/of douche en een bureau. Gasten kunnen genieten van een Marshall-luidspreker, Zenology-spa-producten en uitzicht op de stad, de tuin, het dakraam of het paleis.', 1),
                                                                                                                                                                 (5, 'Wabi Sabi Zeeland', 1, 10, 'Zeeland', 'Scherpenisse', 'Gorishoeksedijk 15', '4694PJ', 8, 4, 150, 'Deze leuke vrijstaande recreatiewoning bevindt zich op fietsafstand van de Oesterdam en op wandelafstand van natuurgebied De Pluimpot en de Oosterschelde met twee kleine strandjes, waar je kan genieten van de rust & ruimte van het platteland.', 0);

INSERT INTO HouseExtraFeature(houseId,featureId) VALUES
                                                     (1,1),
                                                     (2,1),
                                                     (3,1),
                                                     (4,1),
                                                     (5,1),
                                                     (1,2),
                                                     (3,2),
                                                     (5,2),
                                                     (1,3),
                                                     (2,3),
                                                     (3,3),
                                                     (5,3),
                                                     (2,4),
                                                     (4,4),
                                                     (2,5),
                                                     (3,6),
                                                     (4,6),
                                                     (3,9),
                                                     (5,9),
                                                     (5,10);


INSERT INTO HouseType(houseTypeId,houseTypeName) VALUES
                                                     (1,'Woning'),
                                                     (2,'Appartement'),
                                                     (3,'Gastenverblijf'),
                                                     (4,'Hotel');


INSERT INTO Picture(pictureId,houseId,picture,pictureDescription) VALUES
                                                                       (1,1,'resources/images/house1.png','houseId1'),
                                                                       (2,2,'resources/images/house2.png','houseId2'),
                                                                       (3,3,'resources/images/house3.png','houseId3'),
                                                                       (4,4,'resources/images/house4.png','houseId4'),
                                                                       (5,5,'resources/images/house5.png','houseId5');

INSERT INTO Reservation (reservationId,reservedByUserId,houseId,startDate,endDate,guestCount) VALUES
                                                                                                  (1,1,1,'2024-08-08','2024-08-10',2),
                                                                                                  (2,2,2,'2024-09-10','2024-09-15',3),
                                                                                                  (3,3,3,'2024-10-09','2024-10-12',2),
                                                                                                  (4,4,4,'2024-11-10','2024-11-15',1),
                                                                                                  (5,5,5,'2024-12-10','2024-12-12',4);









