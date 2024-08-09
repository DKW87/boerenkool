INSERT INTO `User` (typeOfUser,username,hashedPassword,firstName,infix,lastName,coinBalance,phoneNumber,emailaddress) VALUES
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