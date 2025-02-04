package com.example.prep;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Magic {
	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/prepwell"; // Update if needed
		String user = "root"; // Update if needed
		String password = "root"; // Update if needed

		String[][] mcqs = {
				// === Technical (DSA, OS, DBMS) - Type: "core" ===
				{ "What is the time complexity of binary search?", "O(n)", "O(log n)", "O(n log n)", "O(1)", "O(log n)",
						"core" },
				{ "Which data structure uses LIFO?", "Queue", "Stack", "Heap", "Array", "Stack", "core" },
				{ "Which sorting algorithm is fastest in the average case?", "Bubble Sort", "Merge Sort",
						"Insertion Sort", "Selection Sort", "Merge Sort", "core" },
				{ "What is a deadlock in OS?", "Infinite loop", "Process waiting indefinitely", "Memory leak",
						"Thread blocking", "Process waiting indefinitely", "core" },
				{ "Which SQL command is used to remove a table?", "DELETE", "DROP", "TRUNCATE", "REMOVE", "DROP",
						"core" },
				{ "Which normal form eliminates partial dependency?", "1NF", "2NF", "3NF", "BCNF", "2NF", "core" },
				{ "Which data structure is used in recursion?", "Stack", "Queue", "Heap", "Linked List", "Stack",
						"core" },
				{ "Which protocol is used for file transfer?", "HTTP", "FTP", "SMTP", "TCP", "FTP", "core" },
				{ "What is a semaphore?", "Loop", "Variable", "Synchronization tool", "Data structure",
						"Synchronization tool", "core" },
				{ "What does RAID stand for?", "Random Access Integrated Drive", "Redundant Array of Independent Disks",
						"Reliable Advanced Integrated Data", "Read Access Integrated Disk",
						"Redundant Array of Independent Disks", "core" },
				{ "Which SQL command is used to update data?", "UPDATE", "MODIFY", "CHANGE", "EDIT", "UPDATE", "core" },
				{ "Which OS is open-source?", "Windows", "Linux", "MacOS", "Unix", "Linux", "core" },
				{ "What is the primary key used for?", "Sorting", "Uniquely identifying records", "Indexing",
						"Speeding up queries", "Uniquely identifying records", "core" },
				{ "What is a process in OS?", "A thread", "A running program", "A system call", "A file",
						"A running program", "core" },
				{ "Which database is NoSQL?", "MySQL", "MongoDB", "PostgreSQL", "Oracle", "MongoDB", "core" },
				{ "Which data structure works on FIFO?", "Queue", "Stack", "Heap", "Graph", "Queue", "core" },
				{ "What does DNS stand for?", "Data Network System", "Domain Name System", "Digital Network Security",
						"Dynamic Naming Service", "Domain Name System", "core" },
				{ "What is the default isolation level in MySQL?", "Read Uncommitted", "Read Committed",
						"Repeatable Read", "Serializable", "Repeatable Read", "core" },
				{ "Which layer of OSI model handles encryption?", "Network", "Transport", "Session", "Presentation",
						"Presentation", "core" },
				{ "What is the time complexity of bubble sort?", "O(n)", "O(n^2)", "O(log n)", "O(n log n)", "O(n^2)",
						"core" },
				{ "Which data structure is used to implement recursion?", "Stack", "Queue", "List", "Hash Table",
						"Stack", "core" },
				{ "What is the primary function of the OS?", "Managing files", "Managing hardware resources",
						"Running applications", "Connecting to the internet", "Managing hardware resources", "core" },
				{ "What is the difference between TCP and UDP?", "TCP is faster", "TCP is reliable", "UDP is reliable",
						"TCP has higher overhead", "TCP is reliable", "core" },
				{ "What is the purpose of a foreign key in a database?", "To create indexes",
						"To create a relationship between tables", "To delete data", "To optimize queries",
						"To create a relationship between tables", "core" },
				{ "Which command is used to list all tables in MySQL?", "SHOW TABLES", "LIST TABLES", "DISPLAY TABLES",
						"VIEW TABLES", "SHOW TABLES", "core" },
				{ "What is an algorithm?", "A programming language", "A set of instructions to solve a problem",
						"A type of database", "A system call", "A set of instructions to solve a problem", "core" },
				{ "Which SQL command is used to retrieve data?", "GET", "SELECT", "FETCH", "RETRIEVE", "SELECT",
						"core" },
				{ "What is the use of the 'ORDER BY' clause in SQL?", "To sort the data", "To filter the data",
						"To join tables", "To group the data", "To sort the data", "core" },
				{ "Which data structure is used to implement a breadth-first search?", "Stack", "Queue", "Linked List",
						"Heap", "Queue", "core" },
				{ "Which sorting algorithm is based on the divide and conquer strategy?", "Quick Sort", "Bubble Sort",
						"Merge Sort", "Insertion Sort", "Merge Sort", "core" },
				{ "What is the purpose of a hash table?", "To store data in a linear array",
						"To store data in key-value pairs", "To perform arithmetic calculations",
						"To optimize database queries", "To store data in key-value pairs", "core" },
				{ "Which type of memory is used for cache?", "RAM", "ROM", "Flash", "Static RAM", "Static RAM",
						"core" },
				{ "What is the purpose of the 'GROUP BY' clause in SQL?", "To filter data", "To sort data",
						"To group data by a column", "To join tables", "To group data by a column", "core" },
				{ "Which of the following is an example of an OS?", "Windows", "Chrome", "Firefox", "Java", "Windows",
						"core" },
				{ "What does the 'SELECT DISTINCT' command do in SQL?", "Selects all rows", "Selects unique rows",
						"Selects rows based on a condition", "Selects the top row", "Selects unique rows", "core" },
				{ "Which of these is a type of NoSQL database?", "MySQL", "MongoDB", "PostgreSQL", "SQLite", "MongoDB",
						"core" },
				{ "What does an index do in a database?", "Speeds up data retrieval", "Speeds up data insertion",
						"Optimizes database storage", "Prevents data duplication", "Speeds up data retrieval", "core" },
				{ "What is the purpose of a constructor in object-oriented programming?", "To initialize objects",
						"To destroy objects", "To create objects", "To store objects", "To initialize objects",
						"core" },
				{ "Which protocol is used for sending emails?", "SMTP", "HTTP", "FTP", "TCP", "SMTP", "core" },
				{ "What is the purpose of DNS?", "To resolve IP addresses", "To optimize network performance",
						"To route traffic", "To store website data", "To resolve IP addresses", "core" },
				{ "What is the time complexity of insertion sort?", "O(n)", "O(n^2)", "O(log n)", "O(n log n)",
						"O(n^2)", "core" },
				{ "Which command is used to remove data in SQL?", "DELETE", "DROP", "TRUNCATE", "REMOVE", "DELETE",
						"core" },
				{ "Which programming paradigm is used in Java?", "Procedural", "Object-oriented", "Functional",
						"Declarative", "Object-oriented", "core" },
				{ "Which data structure is used to represent a tree?", "Array", "Queue", "Stack", "Linked List",
						"Linked List", "core" },
				{ "What is an example of a relational database?", "MongoDB", "Oracle", "Cassandra", "Redis", "Oracle",
						"core" },
				{ "What is an operating system?", "A program that manages hardware resources", "A network protocol",
						"A data structure", "A programming language", "A program that manages hardware resources",
						"core" },
				{ "What does the 'LIMIT' clause do in SQL?", "Limits the number of rows returned",
						"Limits the number of tables", "Limits the database size", "Limits the columns returned",
						"Limits the number of rows returned", "core" },
				{ "What is the main difference between a stack and a queue?", "A stack uses FIFO, a queue uses LIFO",
						"A stack uses LIFO, a queue uses FIFO", "A stack is dynamic, a queue is static",
						"A stack is faster", "A stack uses LIFO, a queue uses FIFO", "core" },
				{ "Which layer of the OSI model is responsible for routing?", "Network", "Transport", "Session",
						"Application", "Network", "core" },
				{ "What is the main function of a database index?", "To speed up data retrieval",
						"To increase data redundancy", "To store data in tables", "To join tables",
						"To speed up data retrieval", "core" },
				// === English - Type: "eng" ===
				{ "Choose the correct synonym for 'benevolent'", "Cruel", "Generous", "Harsh", "Rude", "Generous",
						"eng" },
				{ "Identify the antonym of 'ephemeral'", "Permanent", "Short-lived", "Momentary", "Brief", "Permanent",
						"eng" },
				{ "Which sentence is correct?", "She don't like apples.", "She doesn't likes apples.",
						"She doesn't like apples.", "She do not like apples.", "She doesn't like apples.", "eng" },
				{ "Which sentence is grammatically correct?", "He go to school.", "He goes to school.",
						"He going to school.", "He goed to school.", "He goes to school.", "eng" },
				{ "Choose the correct word: 'The sun ____ in the east.'", "rise", "rises", "rised", "raising", "rises",
						"eng" },
				{ "Find the correct plural: 'Child'", "Childs", "Children", "Childes", "Childies", "Children", "eng" },
				{ "Choose the correct spelling:", "Tommorrow", "Tommorow", "Tomorrow", "Tomorow", "Tomorrow", "eng" },
				{ "Which word means the same as 'huge'?", "Tiny", "Small", "Massive", "Little", "Massive", "eng" },
				{ "What is the past tense of 'go'?", "Go", "Goes", "Gone", "Went", "Went", "eng" },
				{ "Find the adjective: 'The quick brown fox jumps over the lazy dog.'", "fox", "jumps", "quick", "over",
						"quick", "eng" },
				{ "Identify the correct preposition: 'He is sitting ___ the chair.'", "in", "on", "at", "over", "on",
						"eng" },
				{ "Which sentence is correct?", "I has a pen.", "I have a pen.", "I having a pen.", "I had a pen.",
						"I have a pen.", "eng" },
				{ "Choose the correct article: 'She is ____ honest person.'", "a", "an", "the", "some", "an", "eng" },
				{ "What is the opposite of 'brave'?", "Bold", "Cowardly", "Strong", "Fearless", "Cowardly", "eng" },
				{ "Identify the noun: 'The dog barked loudly.'", "dog", "barked", "loudly", "the", "dog", "eng" },
				{ "What is the synonym for 'happy'?", "Sad", "Joyful", "Angry", "Grumpy", "Joyful", "eng" },
				{ "Choose the correct tense: 'She ____ to school every day.'", "go", "goes", "gone", "going", "goes",
						"eng" },
				{ "Which word is a verb?", "Running", "Fast", "Blue", "Sky", "Running", "eng" },
				{ "Identify the preposition: 'The cat jumped ____ the table.'", "on", "under", "through", "over", "on",
						"eng" },
				{ "Which sentence is correct?", "He don’t like it.", "He doesn't likes it.", "He doesn't like it.",
						"He not like it.", "He doesn't like it.", "eng" },
				{ "Find the correct plural: 'Foot'", "Feets", "Feet", "Foots", "Foot", "Feet", "eng" },
				{ "What is the opposite of 'rich'?", "Wealthy", "Poor", "Strong", "Smart", "Poor", "eng" },
				{ "What is the antonym of 'soft'?", "Hard", "Smooth", "Gentle", "Rough", "Hard", "eng" },
				{ "Identify the correct spelling:", "Excersize", "Excersise", "Exercise", "Excersize", "Exercise",
						"eng" },
				{ "Which word means 'quickly'?", "Fast", "Slow", "Heavy", "Light", "Fast", "eng" },
				{ "What is the past tense of 'run'?", "Ran", "Run", "Running", "Runned", "Ran", "eng" },
				{ "Which sentence is correct?", "I can speaks English.", "I can speak English.",
						"I can speaking English.", "I can speak English.", "I can speak English.", "eng" },
				{ "Find the adjective: 'She wore a beautiful dress.'", "wore", "a", "beautiful", "dress", "beautiful",
						"eng" },
				{ "Choose the synonym for 'happy':", "Sad", "Excited", "Angry", "Joyful", "Joyful", "eng" },
				{ "What is the opposite of 'cold'?", "Hot", "Warm", "Freezing", "Cool", "Hot", "eng" },
				{ "Which word is a noun?", "Walk", "Beautiful", "House", "Fast", "House", "eng" },
				{ "Identify the antonym of 'early'", "Late", "On time", "Prompt", "Slow", "Late", "eng" },
				{ "Find the adverb in the sentence: 'He ran quickly.'", "He", "ran", "quickly", "quickly", "quickly",
						"eng" },
				{ "Choose the correct word: 'She ____ to the store.'", "go", "goes", "gone", "going", "goes", "eng" },
				{ "What is the synonym of 'strong'?", "Weak", "Powerful", "Fragile", "Brittle", "Powerful", "eng" },
				{ "What is the past tense of 'eat'?", "Eat", "Eats", "Eating", "Ate", "Ate", "eng" },
				{ "Find the correct preposition: 'The cat is sitting ___ the chair.'", "under", "in", "on", "at", "on",
						"eng" },
				{ "Which sentence is grammatically correct?", "They is playing football.", "They are playing football.",
						"They playing football.", "They are plays football.", "They are playing football.", "eng" },
				{ "What is the opposite of 'light'?", "Heavy", "Bright", "Dark", "Clear", "Dark", "eng" },
				{ "What is the synonym of 'intelligent'?", "Dumb", "Smart", "Quick", "Slow", "Smart", "eng" },
				{ "Which word is an adverb?", "Run", "Happiness", "Fast", "Jump", "Fast", "eng" },
				{ "Choose the correct spelling:", "Recieve", "Receive", "Recive", "Reieve", "Receive", "eng" },
				{ "What is the plural of 'mouse'?", "Mouses", "Mice", "Mouse", "Mice", "Mice", "eng" },
				{ "Which sentence is correct?", "I has a dog.", "I have a dog.", "I haves a dog.", "I had a dog.",
						"I have a dog.", "eng" },
				{ "Choose the correct word: 'She ____ a letter yesterday.'", "writes", "wrote", "write", "writing",
						"wrote", "eng" },
				{ "What is the antonym of 'happy'?", "Sad", "Excited", "Joyful", "Angry", "Sad", "eng" },
				{ "Find the correct preposition: 'The book is ___ the table.'", "on", "at", "in", "over", "on", "eng" },
				{ "Which word is a verb?", "Dance", "Quick", "Slow", "Yellow", "Dance", "eng" },
				{ "What is the opposite of 'love'?", "Like", "Hate", "Admire", "Respect", "Hate", "eng" },
				{ "Choose the correct sentence:", "She can sings well.", "She sings can well.", "She can sing well.",
						"She can sing good.", "She can sing well.", "eng" },
				{ "Identify the correct article: '____ apple a day keeps the doctor away.'", "A", "An", "The", "Some",
						"An", "eng" },

				// === Aptitude - Type: "apt" ===
				{ "What is 15% of 200?", "20", "25", "30", "35", "30", "apt" },
				{ "A train travels 60 km in 90 minutes. What is its speed in km/hr?", "30", "40", "50", "60", "40",
						"apt" },
				{ "If a = 5, b = 2, find (a^2 - b^2).", "21", "23", "24", "25", "21", "apt" },
				{ "A shopkeeper sells an item for $120, making a profit of 20%. What is the cost price?", "$100",
						"$110", "$115", "$120", "$100", "apt" },
				{ "If the sum of three consecutive numbers is 78, find the largest number.", "25", "26", "27", "28",
						"27", "apt" },
				{ "What is the value of (2 + 3) × 4?", "14", "20", "10", "24", "20", "apt" },
				{ "Find the missing number: 2, 4, 8, 16, ?", "24", "32", "30", "36", "32", "apt" },
				{ "The average of 10, 20, and 30 is?", "15", "20", "25", "30", "20", "apt" },
				{ "How many degrees are there in a right angle?", "90", "45", "180", "360", "90", "apt" },
				{ "If a person walks 5 km in 1 hour, how far will they walk in 4 hours?", "10 km", "15 km", "20 km",
						"25 km", "20 km", "apt" },
				{ "What is 50% of 160?", "60", "70", "80", "90", "80", "apt" },
				{ "What is the area of a square with side length 8?", "64", "72", "80", "96", "64", "apt" },
				{ "If x = 3, y = 4, find x^2 + y^2.", "10", "12", "14", "16", "25", "apt" },
				{ "A person spends $200 and gets 25% discount. What is the original price?", "$250", "$220", "$230",
						"$200", "$250", "apt" },
				{ "If 2x = 10, what is the value of x?", "3", "4", "5", "6", "5", "apt" },
				{ "A box contains 12 red balls, 8 blue balls, and 10 green balls. What is the total number of balls?",
						"30", "28", "32", "40", "30", "apt" },
				{ "What is the perimeter of a rectangle with length 12 and width 7?", "34", "38", "40", "42", "38",
						"apt" },
				{ "If a number is divisible by both 2 and 3, what is it divisible by?", "6", "4", "5", "7", "6",
						"apt" },
				{ "Find the next number in the sequence: 1, 3, 5, 7, ?", "9", "8", "10", "11", "9", "apt" },
				{ "A man buys a watch for $50 and sells it for $60. What is his profit?", "$10", "$15", "$20", "$25",
						"$10", "apt" },
				{ "How many sides does a hexagon have?", "6", "8", "10", "12", "6", "apt" },
				{ "What is the value of 7 × 6?", "36", "42", "48", "54", "42", "apt" },
				{ "A person saves $500 a month. How much will they save in 12 months?", "$6000", "$5000", "$7000",
						"$8000", "$6000", "apt" },
				{ "If 3x = 12, what is the value of x?", "3", "4", "5", "6", "4", "apt" },
				{ "How many degrees are there in a full circle?", "360", "180", "270", "90", "360", "apt" },
				{ "What is the area of a triangle with base 6 and height 8?", "24", "30", "36", "40", "24", "apt" },
				{ "If a person earns $5000 per month, what is their annual income?", "$60000", "$50000", "$55000",
						"$65000", "$60000", "apt" },
				{ "What is 25% of 400?", "80", "90", "100", "110", "100", "apt" },
				{ "If the product of two numbers is 72 and one number is 9, what is the other number?", "8", "9", "7",
						"6", "8", "apt" },
				{ "How many months are in a year?", "10", "11", "12", "13", "12", "apt" },
				{ "What is the sum of the angles in a triangle?", "180", "90", "270", "360", "180", "apt" },
				{ "If 15% of a number is 45, what is the number?", "300", "200", "400", "500", "300", "apt" },
				{ "How many minutes are in 3 hours?", "180", "240", "150", "120", "180", "apt" },
				{ "If a car travels 80 km/h, how far will it travel in 3 hours?", "240 km", "260 km", "270 km",
						"200 km", "240 km", "apt" },
				{ "A person buys an item for $200 and sells it for $250. What is the profit percentage?", "25%", "30%",
						"35%", "40%", "25%", "apt" },
				{ "What is the square root of 144?", "10", "11", "12", "13", "12", "apt" },
				{ "How many grams are in 1 kilogram?", "500", "1000", "1500", "2000", "1000", "apt" },
				{ "If a person is 5 feet tall, how many inches tall are they?", "60", "65", "70", "75", "60", "apt" },
				{ "What is 9 × 9?", "72", "81", "90", "100", "81", "apt" },
				{ "A person buys 3 apples for $5. How much will they pay for 9 apples?", "$15", "$10", "$20", "$25",
						"$15", "apt" },
				{ "How many hours are in a day?", "24", "12", "30", "36", "24", "apt" },
				{ "If a book costs $12 and a person buys 5 books, how much do they spend?", "$50", "$60", "$55", "$70",
						"$60", "apt" },
				{ "What is 5% of 200?", "5", "10", "15", "20", "10", "apt" },
				{ "How many days are there in February in a leap year?", "28", "30", "31", "29", "29", "apt" },
				{ "What is 100 divided by 4?", "20", "25", "30", "35", "25", "apt" },
				{ "How many kilometers are in a mile?", "1.6", "2.0", "1.0", "1.2", "1.6", "apt" },
				{ "What is the volume of a cube with side length 5?", "25", "50", "125", "100", "125", "apt" },
				{ "If 6x = 36, what is the value of x?", "4", "5", "6", "7", "6", "apt" },
				{ "What is the product of 8 × 12?", "96", "98", "100", "102", "96", "apt" },
				{ "If a person has 3 coins and each coin is worth $0.50, how much money do they have?", "$1", "$1.50",
						"$2", "$2.50", "$1.50", "apt" },
				{ "What is the cost of 8 items if each item costs $7?", "$56", "$60", "$70", "$80", "$56", "apt" } };

		String cdata[][] = {
				{ "Oracle", "2 hrs", "Medium", "https://i.ibb.co/h1xSLjVw/oracle.png", "1", "15", "15", "15" },
				{ "JP Morgan", "1 hr 30 mins", "Medium", "https://i.ibb.co/h1xSLjVw/oracle.png", "2", "0", "0", "0" },
				{ "Dolat Capital", "1 hr 30 mins", "Easy - Medium", "https://i.ibb.co/h1xSLjVw/oracle.png", "3", "0",
						"10", "0" },
				{ "Goldman Sachs", "3 hrs", "Hard", "https://i.ibb.co/h1xSLjVw/oracle.png", "2", "10", "20", "20" },
				{ "Microsoft", "2 hrs 30 mins", "Medium", "https://i.ibb.co/h1xSLjVw/oracle.png", "2", "12", "18",
						"15" },
				{ "Amazon", "2 hrs", "Hard", "https://i.ibb.co/h1xSLjVw/oracle.png", "2", "20", "20", "25" },
				{ "Facebook", "1 hr 45 mins", "Medium", "https://i.ibb.co/h1xSLjVw/oracle.png", "2", "18", "15", "20" },
				{ "Apple", "1 hr 30 mins", "Medium", "https://i.ibb.co/h1xSLjVw/oracle.png", "2", "10", "12", "15" } };

		String qdata[][] = { { "Apples and Bananas",
				"You have a apples and b bananas, you wonder how many fruits you have in total.\n\nInput: The first line has two space separated numbers: a and b\n\nOutput: Output a single line containing the total number of fruits you have\n\n1 <= a <= 100\n1 <= b <= 100",
				"Brute Force", "1", "4" },
				{ "Sum of Digits",
						"You are given a number n. Find the sum of its digits.\n\nInput: A single integer n\n\nOutput: Output the sum of the digits of n\n\n1 <= n <= 10000",
						"Brute Force", "5", "8" },
				{ "Prime Check",
						"You are given a number n. Determine whether it is prime or not.\n\nInput: A single integer n\n\nOutput: Output 'Yes' if n is prime, else 'No'\n\n2 <= n <= 10000",
						"Math", "9", "12" },
				{ "Fibonacci Sequence",
						"You are given a number n. Print the first n numbers in the Fibonacci sequence.\n\nInput: A single integer n\n\nOutput: Output the first n Fibonacci numbers\n\n1 <= n <= 50",
						"Dynamic Programming", "13", "16" },
				{ "Palindrome Check",
						"You are given a string. Check if it is a palindrome.\n\nInput: A single string\n\nOutput: Output 'Yes' if the string is a palindrome, else 'No'\n\n1 <= len(string) <= 100",
						"Strings", "17", "20" },
				{ "Factorial",
						"You are given a number n. Calculate its factorial.\n\nInput: A single integer n\n\nOutput: Output the factorial of n\n\n1 <= n <= 20",
						"Strings", "21", "24" },
				{ "Find Maximum",
						"You are given an array of integers. Find the maximum value in the array.\n\nInput: The first line contains an integer n, followed by n space-separated integers\n\nOutput: Output the maximum value in the array\n\n1 <= n <= 1000",
						"Brute Force", "25", "28" } };

		String tdata[][] = { { "25 30", "55" }, { "20 55", "75" }, { "59 87", "146" }, { "11 12", "23" },

				{ "12345", "15" }, { "9876", "30" }, { "10001", "2" }, { "5555", "20" },

				{ "7", "Yes" }, { "10", "No" }, { "17", "Yes" }, { "25", "No" },

				{ "5", "0 1 1 2 3" }, { "8", "0 1 1 2 3 5 8 13" }, { "3", "0 1 1" }, { "10", "0 1 1 2 3 5 8 13 21 34" },

				{ "madam", "Yes" }, { "hello", "No" }, { "racecar", "Yes" }, { "level", "Yes" },

				{ "5", "120" }, { "7", "5040" }, { "3", "6" }, { "6", "720" },

				{ "5\n1 3 5 2 4", "5" }, { "3\n9 2 7", "9" }, { "4\n6 8 1 10", "10" },
				{ "6\n12 11 19 10 9 21", "21" } };
		try (Connection conn = DriverManager.getConnection(url, user, password)) {
			// Create tables
			String createCompanyTable = """
					CREATE TABLE IF NOT EXISTS company (
					    id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
					    name TEXT,
					    length TEXT,
					    difficulty TEXT,
					    link TEXT,
					    dsa INT,
					    aptitude INT,
					    core INT,
					    english INT
					)""";
			conn.prepareStatement(createCompanyTable).executeUpdate();

			String createQuestionsTable = """
					CREATE TABLE IF NOT EXISTS questions (
					    id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
					    name TEXT,
					    description TEXT,
					    tag TEXT,
					    testcase_start TEXT,
					    testcase_end TEXT
					)""";
			conn.prepareStatement(createQuestionsTable).executeUpdate();

			String createTestcasesTable = """
					CREATE TABLE IF NOT EXISTS testcases (
					    id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
					    input TEXT,
					    output TEXT
					)""";
			conn.prepareStatement(createTestcasesTable).executeUpdate();

			String createMcqsTable = """
					CREATE TABLE IF NOT EXISTS mcqs (
					    id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
					    description TEXT,
					    option1 TEXT,
					    option2 TEXT,
					    option3 TEXT,
					    option4 TEXT,
					    answer TEXT,
					    type TEXT
					)""";
			conn.prepareStatement(createMcqsTable).executeUpdate();

			System.out.println("Tables created successfully!");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Insert Data
		try (Connection conn = DriverManager.getConnection(url, user, password)) {
			String sql = "INSERT INTO company (name, length, difficulty, link, dsa, aptitude, core, english) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			for (String[] c : cdata) {
				for (int i = 0; i < c.length; i++) {
					pstmt.setString(i + 1, c[i]);
				}
				pstmt.executeUpdate();
			}
			System.out.println("Company Data inserted successfully!");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try (Connection conn = DriverManager.getConnection(url, user, password)) {
			String sql = "INSERT INTO questions (name, description, tag, testcase_start, testcase_end) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			for (String[] c : qdata) {
				for (int i = 0; i < c.length; i++) {
					pstmt.setString(i + 1, c[i]);
				}
				pstmt.executeUpdate();
			}
			System.out.println("Question Data inserted successfully!");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try (Connection conn = DriverManager.getConnection(url, user, password)) {
			String sql = "INSERT INTO testcases (input, output) VALUES (?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			for (String[] c : tdata) {
				for (int i = 0; i < c.length; i++) {
					pstmt.setString(i + 1, c[i]);
				}
				pstmt.executeUpdate();
			}
			System.out.println("Test Data inserted successfully!");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try (Connection conn = DriverManager.getConnection(url, user, password)) {
			String sql = "INSERT INTO mcqs (description, option1, option2, option3, option4, answer, type) VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			for (String[] mcq : mcqs) {
				for (int i = 0; i < 7; i++) {
					pstmt.setString(i + 1, mcq[i]);
				}
				pstmt.executeUpdate();
			}
			System.out.println("MCQs inserted successfully!");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
