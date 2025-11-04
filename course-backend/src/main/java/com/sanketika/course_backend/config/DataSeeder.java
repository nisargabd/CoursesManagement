package com.sanketika.course_backend.config;

import com.sanketika.course_backend.dto.CourseDto;
import com.sanketika.course_backend.dto.UnitDto;
import com.sanketika.course_backend.repositories.CourseRepository;
import com.sanketika.course_backend.services.CourseService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
// import java.util.Random;

/**
 * This class seeds the database with diverse, meaningful sample courses when the app starts.
 * Only runs if the database is empty.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final CourseService courseService;
    private final CourseRepository courseRepository;
    // private final Random random = new Random();

    public DataSeeder(CourseService courseService, CourseRepository courseRepository) {
        this.courseService = courseService;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) {
        // Check if database already has courses
        long existingCourseCount = courseRepository.count();
        
        if (existingCourseCount > 0) {
            System.out.println("📚 Database already contains " + existingCourseCount + " courses, skipping seeding");
            return;
        }

        System.out.println("🚀 Starting data seeding...");

        // Define comprehensive course data
        List<CourseData> courseDataList = Arrays.asList(
            // Science Courses
            new CourseData("Physics Fundamentals", "Master the basic principles of physics including mechanics, thermodynamics, and electromagnetism.", "CBSE", 
                Arrays.asList("English"), Arrays.asList("11", "12"), Arrays.asList("Physics"),
                Arrays.asList(
                    new UnitDto("Mechanics", "Learn about motion, forces, and energy in classical mechanics."),
                    new UnitDto("Thermodynamics", "Understand heat, temperature, and energy transfer."),
                    new UnitDto("Electromagnetism", "Explore electric and magnetic fields and their interactions.")
                )),
            
            new CourseData("Chemistry Essentials", "Comprehensive chemistry course covering organic, inorganic, and physical chemistry.", "CBSE",
                Arrays.asList("English"), Arrays.asList("11", "12"), Arrays.asList("Chemistry"),
                Arrays.asList(
                    new UnitDto("Atomic Structure", "Understanding atoms, electrons, and periodic properties."),
                    new UnitDto("Chemical Bonding", "Learn about ionic, covalent, and metallic bonds."),
                    new UnitDto("Organic Chemistry", "Study carbon compounds and their reactions.")
                )),

            new CourseData("Biology Mastery", "Complete biology course covering cell biology, genetics, and evolution.", "CBSE",
                Arrays.asList("English"), Arrays.asList("11", "12"), Arrays.asList("Biology"),
                Arrays.asList(
                    new UnitDto("Cell Biology", "Study the structure and function of cells."),
                    new UnitDto("Genetics", "Learn about heredity and genetic variation."),
                    new UnitDto("Evolution", "Understand the process of biological evolution.")
                )),

            // Mathematics Courses
            new CourseData("Advanced Mathematics", "Comprehensive mathematics course covering calculus, algebra, and statistics.", "CBSE",
                Arrays.asList("English"), Arrays.asList("11", "12"), Arrays.asList("Maths"),
                Arrays.asList(
                    new UnitDto("Calculus", "Learn differential and integral calculus."),
                    new UnitDto("Linear Algebra", "Study vectors, matrices, and linear transformations."),
                    new UnitDto("Statistics", "Understand probability and statistical analysis.")
                )),

            new CourseData("Mathematics for Engineers", "Engineering mathematics focusing on applied calculus and differential equations.", "CBSE",
                Arrays.asList("English"), Arrays.asList("11", "12"), Arrays.asList("Maths"),
                Arrays.asList(
                    new UnitDto("Applied Calculus", "Practical applications of calculus in engineering."),
                    new UnitDto("Differential Equations", "Solving differential equations in engineering problems."),
                    new UnitDto("Complex Analysis", "Study of complex numbers and functions.")
                )),

            // Computer Science Courses
            new CourseData("Python Programming", "Learn Python programming from basics to advanced concepts.", "CBSE",
                Arrays.asList("English"), Arrays.asList("9", "10", "11", "12"), Arrays.asList("Computer"),
                Arrays.asList(
                    new UnitDto("Python Basics", "Introduction to Python syntax and basic programming concepts."),
                    new UnitDto("Data Structures", "Learn about lists, dictionaries, and other data structures."),
                    new UnitDto("Object-Oriented Programming", "Master classes, objects, and inheritance in Python.")
                )),

            new CourseData("Web Development", "Complete web development course covering HTML, CSS, and JavaScript.", "CBSE",
                Arrays.asList("English"), Arrays.asList("10", "11", "12"), Arrays.asList("Computer"),
                Arrays.asList(
                    new UnitDto("HTML Fundamentals", "Learn HTML structure and semantic markup."),
                    new UnitDto("CSS Styling", "Master CSS for beautiful web design."),
                    new UnitDto("JavaScript Programming", "Add interactivity with JavaScript.")
                )),

            new CourseData("Data Science", "Introduction to data science, machine learning, and data analysis.", "CBSE",
                Arrays.asList("English"), Arrays.asList("11", "12"), Arrays.asList("Computer"),
                Arrays.asList(
                    new UnitDto("Data Analysis", "Learn to analyze and visualize data."),
                    new UnitDto("Machine Learning", "Introduction to ML algorithms and applications."),
                    new UnitDto("Big Data", "Understanding large-scale data processing.")
                )),

            // Language Courses
            new CourseData("English Literature", "Explore classic and modern English literature.", "CBSE",
                Arrays.asList("English"), Arrays.asList("9", "10", "11", "12"), Arrays.asList("English"),
                Arrays.asList(
                    new UnitDto("Poetry Analysis", "Understanding poetic devices and themes."),
                    new UnitDto("Novel Studies", "Analysis of classic novels and their themes."),
                    new UnitDto("Drama", "Study of dramatic literature and techniques.")
                )),

            new CourseData("Hindi Literature", "Comprehensive Hindi literature course covering poetry, prose, and drama.", "CBSE",
                Arrays.asList("Hindi"), Arrays.asList("9", "10", "11", "12"), Arrays.asList("Hindi"),
                Arrays.asList(
                    new UnitDto("कविता", "हिंदी कविता का अध्ययन और विश्लेषण।"),
                    new UnitDto("गद्य", "हिंदी गद्य साहित्य का अध्ययन।"),
                    new UnitDto("नाटक", "हिंदी नाटक और रंगमंच का अध्ययन।")
                )),

            new CourseData("Kannada Literature", "Study of Kannada literature, poetry, and cultural heritage.", "State",
                Arrays.asList("Kannada"), Arrays.asList("9", "10", "11", "12"), Arrays.asList("Kannada"),
                Arrays.asList(
                    new UnitDto("ಕನ್ನಡ ಕವಿತೆ", "ಕನ್ನಡ ಕವಿತೆಯ ಅಧ್ಯಯನ ಮತ್ತು ವಿಶ್ಲೇಷಣೆ।"),
                    new UnitDto("ಕನ್ನಡ ಗದ್ಯ", "ಕನ್ನಡ ಗದ್ಯ ಸಾಹಿತ್ಯದ ಅಧ್ಯಯನ।"),
                    new UnitDto("ಕನ್ನಡ ನಾಟಕ", "ಕನ್ನಡ ನಾಟಕ ಮತ್ತು ರಂಗಭೂಮಿಯ ಅಧ್ಯಯನ।")
                )),

            // Social Sciences
            new CourseData("Indian History", "Comprehensive study of Indian history from ancient to modern times.", "CBSE",
                Arrays.asList("English"), Arrays.asList("9", "10", "11", "12"), Arrays.asList("History"),
                Arrays.asList(
                    new UnitDto("Ancient India", "Study of ancient Indian civilizations and empires."),
                    new UnitDto("Medieval India", "Understanding medieval Indian kingdoms and cultures."),
                    new UnitDto("Modern India", "Indian independence movement and post-independence era.")
                )),

            new CourseData("World Geography", "Complete geography course covering physical and human geography.", "CBSE",
                Arrays.asList("English"), Arrays.asList("9", "10", "11", "12"), Arrays.asList("Geography"),
                Arrays.asList(
                    new UnitDto("Physical Geography", "Study of landforms, climate, and natural resources."),
                    new UnitDto("Human Geography", "Understanding population, settlements, and economic activities."),
                    new UnitDto("Environmental Geography", "Study of environmental issues and conservation.")
                )),

            new CourseData("Political Science", "Understanding political systems, governance, and civic responsibilities.", "CBSE",
                Arrays.asList("English"), Arrays.asList("11", "12"), Arrays.asList("Civics"),
                Arrays.asList(
                    new UnitDto("Indian Constitution", "Study of the Indian Constitution and its features."),
                    new UnitDto("Political Theory", "Understanding political concepts and ideologies."),
                    new UnitDto("International Relations", "Study of global politics and diplomacy.")
                )),

            // Commerce Courses
            new CourseData("Business Studies", "Comprehensive business studies course covering management and entrepreneurship.", "CBSE",
                Arrays.asList("English"), Arrays.asList("11", "12"), Arrays.asList("Business Studies"),
                Arrays.asList(
                    new UnitDto("Business Environment", "Understanding the business environment and its factors."),
                    new UnitDto("Management Principles", "Learn about management functions and principles."),
                    new UnitDto("Entrepreneurship", "Study of entrepreneurship and business planning.")
                )),

            new CourseData("Economics", "Complete economics course covering micro and macro economics.", "CBSE",
                Arrays.asList("English"), Arrays.asList("11", "12"), Arrays.asList("Economics"),
                Arrays.asList(
                    new UnitDto("Microeconomics", "Study of individual economic units and market behavior."),
                    new UnitDto("Macroeconomics", "Understanding national economy and economic policies."),
                    new UnitDto("Indian Economy", "Analysis of Indian economic development and challenges.")
                )),

            // Arts Courses
            new CourseData("Fine Arts", "Comprehensive fine arts course covering drawing, painting, and sculpture.", "CBSE",
                Arrays.asList("English"), Arrays.asList("9", "10", "11", "12"), Arrays.asList("Arts"),
                Arrays.asList(
                    new UnitDto("Drawing Fundamentals", "Learn basic drawing techniques and principles."),
                    new UnitDto("Painting Techniques", "Master various painting methods and styles."),
                    new UnitDto("Sculpture", "Introduction to three-dimensional art forms.")
                )),

            new CourseData("Music Theory", "Complete music theory course covering classical and contemporary music.", "CBSE",
                Arrays.asList("English"), Arrays.asList("9", "10", "11", "12"), Arrays.asList("Music"),
                Arrays.asList(
                    new UnitDto("Music Fundamentals", "Learn about notes, scales, and rhythm."),
                    new UnitDto("Classical Music", "Study of classical music traditions."),
                    new UnitDto("Contemporary Music", "Understanding modern music styles and techniques.")
                )),

            // Specialized Courses
            new CourseData("Environmental Science", "Study of environmental issues, conservation, and sustainability.", "CBSE",
                Arrays.asList("English"), Arrays.asList("9", "10", "11", "12"), Arrays.asList("Science"),
                Arrays.asList(
                    new UnitDto("Ecosystems", "Understanding natural ecosystems and their balance."),
                    new UnitDto("Pollution Control", "Study of environmental pollution and control measures."),
                    new UnitDto("Sustainable Development", "Learn about sustainable practices and green technology.")
                )),

            new CourseData("Psychology", "Introduction to psychology covering human behavior and mental processes.", "CBSE",
                Arrays.asList("English"), Arrays.asList("11", "12"), Arrays.asList("Psychology"),
                Arrays.asList(
                    new UnitDto("Introduction to Psychology", "Understanding the basics of psychology as a science."),
                    new UnitDto("Human Development", "Study of human growth and development across lifespan."),
                    new UnitDto("Social Psychology", "Understanding social behavior and group dynamics.")
                )),

            new CourseData("Physical Education", "Comprehensive physical education course covering fitness and sports.", "CBSE",
                Arrays.asList("English"), Arrays.asList("9", "10", "11", "12"), Arrays.asList("Physical Education"),
                Arrays.asList(
                    new UnitDto("Fitness Fundamentals", "Learn about physical fitness and exercise principles."),
                    new UnitDto("Sports Science", "Understanding the science behind sports performance."),
                    new UnitDto("Health and Wellness", "Study of health promotion and disease prevention.")
                )),

            new CourseData("Home Science", "Complete home science course covering nutrition, textiles, and child development.", "CBSE",
                Arrays.asList("English"), Arrays.asList("9", "10", "11", "12"), Arrays.asList("Home Science"),
                Arrays.asList(
                    new UnitDto("Nutrition and Health", "Understanding nutrition principles and healthy eating."),
                    new UnitDto("Textile Science", "Study of fabrics, fibers, and clothing technology."),
                    new UnitDto("Child Development", "Learn about child growth and development.")
                )),

            new CourseData("Agriculture", "Modern agriculture course covering crop production and farm management.", "State",
                Arrays.asList("English", "Kannada"), Arrays.asList("9", "10", "11", "12"), Arrays.asList("Agriculture"),
                Arrays.asList(
                    new UnitDto("Crop Production", "Learn about modern crop production techniques."),
                    new UnitDto("Soil Science", "Understanding soil properties and fertility management."),
                    new UnitDto("Farm Management", "Study of farm planning and agricultural economics.")
                )),

            new CourseData("Tourism Studies", "Comprehensive tourism course covering hospitality and travel management.", "CBSE",
                Arrays.asList("English"), Arrays.asList("11", "12"), Arrays.asList("Tourism"),
                Arrays.asList(
                    new UnitDto("Tourism Industry", "Understanding the global tourism industry."),
                    new UnitDto("Hospitality Management", "Learn about hotel and hospitality services."),
                    new UnitDto("Travel Planning", "Study of travel planning and destination management.")
                ))
        );

        // Create courses from the data
        for (CourseData data : courseDataList) {
            CourseDto dto = new CourseDto();
            dto.setName(data.name);
            dto.setDescription(data.description);
            dto.setBoard(data.board);
            dto.setMedium(data.medium);
            dto.setGrade(data.grade);
            dto.setSubject(data.subject);
            dto.setUnits(data.units);

            try {
            courseService.createCourse(dto);
                System.out.println("✅ Added: " + dto.getName());
            } catch (Exception e) {
                System.err.println("❌ Failed to add: " + dto.getName() + " - " + e.getMessage());
            }
        }

        System.out.println("🎉 Seeding completed! Added " + courseDataList.size() + " diverse courses.");
    }

    // Helper class to organize course data
    private static class CourseData {
        String name;
        String description;
        String board;
        List<String> medium;
        List<String> grade;
        List<String> subject;
        List<UnitDto> units;

        CourseData(String name, String description, String board, List<String> medium, 
                  List<String> grade, List<String> subject, List<UnitDto> units) {
            this.name = name;
            this.description = description;
            this.board = board;
            this.medium = medium;
            this.grade = grade;
            this.subject = subject;
            this.units = units;
        }
    }
}