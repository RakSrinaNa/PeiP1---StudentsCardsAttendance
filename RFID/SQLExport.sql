-- ---------------------------
-- STRUCTURE
-- ---------------------------
DROP TABLE IF EXISTS Students;
CREATE TABLE IF NOT EXISTS Students (CSN VARCHAR(18) NOT NULL, Firstname VARCHAR(100) NOT NULL, Lastname VARCHAR(100) NOT NULL, PRIMARY KEY(CSN));

-- ---------------------------
-- DATA OF STUDENTS
-- ---------------------------
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("000000100AD672", "Richette", "Ludovic");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("000000C8020619", "Robin", "Christopher");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("000000C80554D2", "Vasinis", "Alicia");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("0408387AA63A80", "Augay", "Antoine");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04084B7AA63A80", "Cornu", "Xavier");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("0408528A813A80", "Richard", "Clement");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("040A5E8A813A80", "Hayes", "Jean-philippe");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("040C3E7AA63A80", "Saulnier", "Vincent");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04103EA2813A80", "Mouton", "Julien");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04154DAA813A80", "Gache", "Thomas");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04156BAA813A80", "Gicquel", "Thomas");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("0416639A813A80", "Galliot", "Axel");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04186A9A813A80", "Cosnard", "Valentin");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04191D7AA63A80", "Bilquart-lemercier", "Antoine");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("041B5BA2813A80", "Llavori", "Paul");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("041B647AA63A80", "Lai", "Jiahui");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04232F7AA63A80", "Billay", "Alexandre");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04233A9A813A80", "Girard", "Maxime");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04235CA2813A80", "Doreau", "Clement");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04248A7AA63A80", "Cardineau", "Loic");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("042C457AA63A80", "Delys", "Valentin");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("042D59F2813A80", "Chalopin", "Quentin");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("042F657AA63A80", "Aubrit", "Aurelien");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04345EA2813A80", "Ziegler", "Maxime");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("043726A2813A80", "Pilot", "Erwin");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("043825EA813A80", "Raillet", "Sebastien");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04384C7AA63A80", "Kremer", "Nicolas");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("043B447AA63A80", "Benbouhou", "Mounir");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("043E8A7AA63A80", "Audrerie", "Claire");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("044617EA813A80", "Kadric", "Armin");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("044A33EA813A80", "Lambert", "Florian");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("044B34EA813A80", "Lemee", "Emilie");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("044E1E9A813A80", "Repillez", "Kevin");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("044E238A813A80", "Lespagnol", "Pierre");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("0450747AA63A80", "COUCHOUD", "Thomas");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("0451117AA63A80", "Bertin", "Mathieu");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("0451447AA63A80", "Loyau", "Jason");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04548D7AA63A80", "Bastat", "Wilfried");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("0455538A813A80", "Morand", "Guillaume");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("0456659A813A80", "Muller", "Camille");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("0457089A813A80", "Papin", "Benjamin");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04583A7AA63A80", "Prioul", "Thomas");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("045B577AA63A80", "Yoyotte", "Carla");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("045C4C7AA63A80", "Francheteau", "Vincent");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("045E4C7AA63A80", "Robillard", "Pierre");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("046151A2813A80", "Potier", "Kevin");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("046328A2813A80", "Mohammad", "Anmol");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04635DA2813A80", "Cortassa", "Charlie");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("046369A2813A80", "Nouzille", "Laura");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04637DEA813A80", "Laporte", "Sebastien");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04644C7AA63A80", "Ripoche", "Jacquelin");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04664B7AA63A80", "Lecomte", "Dylan");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04693D7AA63A80", "Raillere", "Simon");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("046A0D9A813A80", "Servant", "Gwenael");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("046A10F2813A80", "Artus", "Thibault");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("046B0DF2813A80", "Ardouin", "Damien");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("046E22F2813A80", "Boeuf", "Remi");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("046E557AA63A80", "Lanon", "William");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04734E7AA63A80", "Robineau", "Baptiste");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("047435A2813A80", "Thomas", "Anthony");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("047455A2813A80", "Lebrun", "Alexandre");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("047460EA813A80", "Karpinski", "Johan");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("047618F2813A80", "Hergault", "Jeremy");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04772FF2813A80", "Chemel", "Thomas");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("047847A2813A80", "Gerard", "Maxime");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("047A2A8A813A80", "Thauvin", "Mathieu");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("047A34F2813A80", "Lambert", "Alexandre");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("047A4CA2813A80", "Laloubeyre", "Clement");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("047C1D7AA63A80", "Coue", "Guillaume");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("047D3D7AA63A80", "Frigout", "Cecilia");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("0483837AA63A80", "Roueil", "Claire");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04866AF2813A80", "El bekkali", "Abdellah");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04883DA2813A80", "Poupeau", "Steven");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("04884BA2813A80", "Patte", "Benjamin");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("048A69A2813A80", "Suteau", "Yann");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("048D4AF2813A80", "Sapiens", "Corentin");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("048E397AA63A80", "Drean", "Julien");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("049026F2813A80", "Hallier", "Bastien");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("049034F2813A80", "Mikiela", "Duffy glenn");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("049136F2813A80", "Lendoye", "Randy martel");
INSERT INTO Students (CSN, Lastname, Firstname) VALUES("0493397AA63A80", "Fournier", "Antoine");

-- ---------------------------
-- STRUCTURE
-- ---------------------------
DROP TABLE IF EXISTS Checked;
CREATE TABLE IF NOT EXISTS Checked (CSN VARCHAR(18) NOT NULL, Date DATE NOT NULL, PRIMARY KEY(CSN, Date));

-- ---------------------------
-- DATA OF CHECKED
-- ---------------------------
INSERT INTO Checked (CSN, Date) VALUES("0450747AA63A80", "2015-04-03");
