package ru.maksarts.sobescracker;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.maksarts.sobescracker.model.*;
import ru.maksarts.sobescracker.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class SobesCrackerIntegrationTests {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private TgUserRepository tgUserRepository;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    @Order(0)
    void contextLoads() {
    }

    @Test
    @Order(1)
    void createTypesTest(){
        String[] typeNames = {"T1", "T2", "T3"};
        List<Type> initTypes = new ArrayList<>();
        for (var typeName : typeNames){
            initTypes.add(Type.builder().name(typeName).build());
        }
        typeRepository.saveAll(initTypes);

        Assertions.assertNotNull(typeRepository.getByNameIgnoreCase("T1").orElse(null));
        Assertions.assertNotNull(typeRepository.getByNameIgnoreCase("T2").orElse(null));
        Assertions.assertNotNull(typeRepository.getByNameIgnoreCase("T3").orElse(null));
    }

    @Test
    @Order(2)
    void createUsersTest(){
        TgUser user = TgUser.builder()
                .chatId(111)
                .name("TestName")
                .nickname("test")
                .build();
        tgUserRepository.save(user);

        Assertions.assertTrue(tgUserRepository.existsByChatId(111));
        Assertions.assertNotNull(tgUserRepository.getTgUserByChatId(111).orElse(null));
        Assertions.assertEquals("TestName", tgUserRepository.getTgUserByChatId(111).orElse(null).getName());
    }

    @Test
    @Order(3)
    void createQuestionsTest(){
        Type t1 = typeRepository.getByNameIgnoreCase("T1").orElse(null);
        Type t2 = typeRepository.getByNameIgnoreCase("T2").orElse(null);
        Type t3 = typeRepository.getByNameIgnoreCase("T3").orElse(null);

        List<Question> initQuestions = new ArrayList<>();
        initQuestions.add(Question.builder()
                .type(t1)
                .grade(QuestionGrade.JUNIOR)
                .content("Content1")
                .answer("Answer1")
                .build()
        );
        initQuestions.add(Question.builder()
                .type(t2)
                .grade(QuestionGrade.MIDDLE)
                .content("Content2")
                .answer("Answer2")
                .build()
        );
        initQuestions.add(Question.builder()
                .type(t3)
                .grade(QuestionGrade.MIDDLE)
                .content("Content3")
                .answer("Answer3")
                .build()
        );
        initQuestions.add(Question.builder()
                .type(t1)
                .grade(QuestionGrade.SENIOR)
                .content("Content4")
                .answer("Answer4")
                .build()
        );
        questionRepository.saveAll(initQuestions);

        List<Question> qt1 = questionRepository.getQuestionByType(t1);
        List<Question> qt2 = questionRepository.getQuestionByType(t2);
        List<Question> qt3 = questionRepository.getQuestionByType(t3);

        Assertions.assertEquals(2, qt1.size());
        Assertions.assertEquals(1, qt2.size());
        Assertions.assertEquals(1, qt3.size());
    }

    @Test
    @Order(4)
    void createCourseTest(){
        List<Type> types = typeRepository.findAll();
        Set<Type> typeSet = types.stream()
                .filter(type -> type.getName().equalsIgnoreCase("T1") || type.getName().equalsIgnoreCase("T2"))
                .collect(Collectors.toSet());

        Course course = Course.builder()
                .name("testCourse")
                .types(typeSet)
                .maxGrade(QuestionGrade.MIDDLE)
                .build();
        courseRepository.save(course);

        Type t1 = typeRepository.getByNameIgnoreCase("T1").orElse(null);
        Type t2 = typeRepository.getByNameIgnoreCase("T2").orElse(null);

        Assertions.assertNotNull(courseRepository.getByName("testCourse").orElse(null));

        courseRepository.getByName("testCourse").ifPresent(course1 -> {
            Assertions.assertEquals(QuestionGrade.MIDDLE.getVal(), course1.getMaxGradeVal());
            Assertions.assertArrayEquals(
                    List.of(t1, t2).toArray(),
                    course1.getTypes().stream().sorted(Comparator.comparing(Type::getName)).toArray());
        });
    }

    @Test
    @Order(5)
    void allQuestionsTest(){
        List<Question> questions1 =
                questionRepository.findAll()
                        .stream()
                        .filter(x ->
                                x.getType().getName().equalsIgnoreCase("T1") ||
                                x.getType().getName().equalsIgnoreCase("T2") ||
                                x.getType().getName().equalsIgnoreCase("T3")
                        )
                        .toList();

        log.info(questions1.toString());

        Assertions.assertEquals(4, questions1.size());

        questions1.forEach(q -> {
            Assertions.assertTrue(
                    q.getType().getName().equalsIgnoreCase("T1") ||
                            q.getType().getName().equalsIgnoreCase("T2") ||
                            q.getType().getName().equalsIgnoreCase("T3"));
        });
    }

    @Test
    @Order(6)
    void questionsByCourseTest(){
        Course course = courseRepository.getByName("testCourse").orElse(null);

        List<Question> questions2 = questionRepository.getQuestionsByCourse(course);

        log.info(questions2.toString());

        Assertions.assertEquals(2, questions2.size());

        questions2.forEach(x -> {
            Assertions.assertNotEquals(x.getType().getName(), "T3");
            Assertions.assertNotEquals(x.getGrade(), QuestionGrade.SENIOR);
        });
    }

    @Test
    @Order(7)
    void excludedQuestionsTest(){
        TgUser user = tgUserRepository.getTgUserByChatId(111).orElse(null);
        Assertions.assertTrue(user.getExcludedQuestions().isEmpty());

        Type t1 = typeRepository.getByNameIgnoreCase("T1").orElse(null);

        Question question = questionRepository.getQuestionByGradeValueAndType(100, t1).get(0);
        user.setExcludedQuestions(Set.of(question));
        tgUserRepository.save(user);

        user = tgUserRepository.getTgUserByChatId(111).orElse(null);
        Assertions.assertFalse(user.getExcludedQuestions().isEmpty());
    }

    @Test
    @Order(8)
    void questionsByCourseAndExcludedQuestionsTest(){
        Course course = courseRepository.getByName("testCourse").orElse(null);
        TgUser user = tgUserRepository.getTgUserByChatId(111).orElse(null);

        List<Question> questions3 = questionRepository.getQuestionByCourseAndExcluded(
                        course,
                        user.getExcludedQuestions(),
                        PageRequest.of(0, 10))
                .get().toList();

        log.info(questions3.toString());

        Assertions.assertEquals(1, questions3.size());
        questions3.forEach(x -> {
            Assertions.assertNotEquals(x.getType().getName(), "T3");
            Assertions.assertNotEquals(x.getGrade(), QuestionGrade.SENIOR);
            Assertions.assertNotEquals(x.getGrade(), QuestionGrade.JUNIOR);
        });

        List<Question> questionsWithoutExclude = questionRepository.getQuestionByCourseAndExcluded(
                        course,
                        Set.of(),
                        PageRequest.of(0, 10))
                .get().toList();

        log.info(questionsWithoutExclude.toString());

        Assertions.assertEquals(2, questionsWithoutExclude.size());
        questionsWithoutExclude.forEach(x -> {
            Assertions.assertNotEquals(x.getType().getName(), "T3");
            Assertions.assertNotEquals(x.getGrade(), QuestionGrade.SENIOR);
        });
    }

    @Test
    @Order(1000)
    void deleteTestData(){
        TgUser user = tgUserRepository.getTgUserByChatId(111).orElseThrow();
        tgUserRepository.delete(user);
        Assertions.assertFalse(tgUserRepository.existsByChatId(111));


        courseRepository.getByName("testCourse").ifPresent(course -> courseRepository.delete(course));
        Assertions.assertNull(courseRepository.getByName("testCourse").orElse(null));


        Type t1 = typeRepository.getByNameIgnoreCase("T1").orElse(null);
        Type t2 = typeRepository.getByNameIgnoreCase("T2").orElse(null);
        Type t3 = typeRepository.getByNameIgnoreCase("T3").orElse(null);
        List<Question> questions =
                questionRepository.findAll()
                        .stream()
                        .filter(x ->
                                x.getType().getName().equalsIgnoreCase("T1") ||
                                x.getType().getName().equalsIgnoreCase("T2") ||
                                x.getType().getName().equalsIgnoreCase("T3")
                        )
                        .toList();
        questionRepository.deleteAll(questions);
        Assertions.assertTrue(questionRepository.getQuestionByType(t1).isEmpty());
        Assertions.assertTrue(questionRepository.getQuestionByType(t2).isEmpty());
        Assertions.assertTrue(questionRepository.getQuestionByType(t3).isEmpty());


        String[] typeNames = {"T1", "T2", "T3"};
        List<Type> types = new ArrayList<>();
        for (var typeName : typeNames){
            types.add(typeRepository.getByNameIgnoreCase(typeName).orElse(null));
        }
        typeRepository.deleteAll(types);
        Assertions.assertNull(typeRepository.getByNameIgnoreCase("T1").orElse(null));
        Assertions.assertNull(typeRepository.getByNameIgnoreCase("T2").orElse(null));
        Assertions.assertNull(typeRepository.getByNameIgnoreCase("T3").orElse(null));
    }

}
