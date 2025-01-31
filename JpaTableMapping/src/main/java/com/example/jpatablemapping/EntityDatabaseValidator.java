package com.example.jpatablemapping;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class EntityDatabaseValidator {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    private final EntityManager entityManager; // ✅ EntityManager 사용

    @PostConstruct
    public void onStartup() {
        log.info("엔티티-DB 검증 시작");
        validateEntities();
        log.info("엔티티-DB 검증 완료");
    }

    public void validateEntities() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Metamodel metamodel = sessionFactory.getMetamodel();

        Set<EntityType<?>> entities = metamodel.getEntities();
        for (EntityType<?> entity : entities) {
            Class<?> entityClass = entity.getJavaType();

            String tableName = getTableName(entityClass);
            if (tableName != null) {
                compareColumnsWithEntity(tableName, entityClass);
            }
        }
    }

    private String getTableName(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        return (table != null) ? table.name() : entityClass.getSimpleName().toLowerCase();
    }

    private void compareColumnsWithEntity(String tableName, Class<?> entityClass) {
        Set<String> dbColumns = getDatabaseColumns(tableName);
        Set<String> entityFields = getEntityFields(entityClass);

        Set<String> extraColumnsInDb = new HashSet<>(dbColumns);
        extraColumnsInDb.removeAll(entityFields);

        Set<String> missingColumnsInDb = new HashSet<>(entityFields);
        missingColumnsInDb.removeAll(dbColumns);

        if (!extraColumnsInDb.isEmpty()) {
            log.warn("[table: " + tableName + "] DB에는 있지만 엔티티에 없는 컬럼 존재: " + extraColumnsInDb);
        }
        if (!missingColumnsInDb.isEmpty()) {
            log.warn("[table: " + tableName + "] 엔티티에는 있지만 DB에는 없는 컬럼 존재: " + missingColumnsInDb);
        }
    }

    // EntityManager를 활용하여 PK, FK 제외 후 DB 컬럼 가져오기
    private Set<String> getDatabaseColumns(String tableName) {
        String sql =
            """
            SELECT COLUMN_NAME
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_NAME = '%s'
            AND COLUMN_NAME NOT IN (
                SELECT COLUMN_NAME
                FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
                WHERE TABLE_NAME = '%s'
            )
            """
            .formatted(tableName, tableName);

        List<String> columns = entityManager.createNativeQuery(sql).getResultList();

        return new HashSet<>(columns);
    }

    // @Id, 연관관계 제외 후 비교
    private Set<String> getEntityFields(Class<?> entityClass) {
        Set<String> fields = new HashSet<>();
        for (var field : entityClass.getDeclaredFields()) {
            // 연관관계 필드 제외
            if (field.isAnnotationPresent(OneToMany.class) ||
                field.isAnnotationPresent(ManyToMany.class)) {
                continue;
            }
            // PK 제외
            if (field.isAnnotationPresent(Id.class)) {
                continue;
            }
            // FK 제외
            if (field.isAnnotationPresent(ManyToOne.class) ||
                field.isAnnotationPresent(OneToOne.class) ||
                field.isAnnotationPresent(JoinColumn.class)) {
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            String columnName = (column != null) ? column.name() : convertCamelToSnake(field.getName());
            fields.add(columnName);
        }
        return fields;
    }

    // CamelCase → SnakeCase 변환 (ex: loginId → login_id)
    private String convertCamelToSnake(String camelCase) {
        return Pattern.compile("([a-z])([A-Z]+)")
            .matcher(camelCase)
            .replaceAll("$1_$2")
            .toLowerCase();
    }
}
