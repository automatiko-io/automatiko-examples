package io.automatiko.examples.support.incidents.index;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.insertInto;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.servererrors.QueryExecutionException;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.Insert;
import com.datastax.oss.driver.api.querybuilder.schema.CreateIndex;
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable;

import io.automatiko.engine.api.event.DataEvent;
import io.automatiko.engine.api.event.EventPublisher;
import io.automatiko.engine.services.event.ProcessInstanceDataEvent;
import io.automatiko.engine.services.event.impl.ProcessInstanceEventBody;
import io.automatiko.examples.support.incidents.Incident;

@ApplicationScoped
public class SupportIncidentsIndex implements EventPublisher {

    private static final String SUPPORT_TEAM_TABLE = "incidents_by_support_team";
    private static final String REPORTER_TABLE = "incidents_by_reporter";

    private CqlSession cqlSession;

    @Inject
    public SupportIncidentsIndex(CqlSession cqlSession) {
        this.cqlSession = cqlSession;
    }

    @PostConstruct
    public void createTables() {
        CreateTable createTable = SchemaBuilder.createTable("automatiko", SUPPORT_TEAM_TABLE)
                .ifNotExists()
                .withPartitionKey("SUPPORT_TEAM", DataTypes.TEXT)
                .withClusteringColumn("INCIDENT_ID", DataTypes.TEXT)
                .withColumn("TITLE", DataTypes.TEXT)
                .withColumn("DESCRIPTION", DataTypes.TEXT)
                .withColumn("SEVERITY", DataTypes.TEXT)
                .withColumn("STATUS", DataTypes.TEXT)
                .withColumn("REPORTER", DataTypes.TEXT)
                .withColumn("REPORTER_COMPANY", DataTypes.TEXT);

        cqlSession.execute(createTable.build());

        CreateIndex index = SchemaBuilder.createIndex(SUPPORT_TEAM_TABLE + "_IDX").ifNotExists()
                .onTable("automatiko", SUPPORT_TEAM_TABLE).andColumn("STATUS");
        cqlSession.execute(index.build());

        CreateTable createTableReporter = SchemaBuilder.createTable("automatiko", REPORTER_TABLE)
                .ifNotExists()
                .withPartitionKey("REPORTER", DataTypes.TEXT)
                .withClusteringColumn("INCIDENT_ID", DataTypes.TEXT)
                .withColumn("TITLE", DataTypes.TEXT)
                .withColumn("DESCRIPTION", DataTypes.TEXT)
                .withColumn("SEVERITY", DataTypes.TEXT)
                .withColumn("STATUS", DataTypes.TEXT)
                .withColumn("NO_OF_COMMENTS", DataTypes.INT);

        cqlSession.execute(createTableReporter.build());

        index = SchemaBuilder.createIndex(REPORTER_TABLE + "_IDX").ifNotExists()
                .onTable("automatiko", REPORTER_TABLE).andColumn("STATUS");
        cqlSession.execute(index.build());
    }

    @Override
    public void publish(DataEvent<?> event) {
        if (event instanceof ProcessInstanceDataEvent) {
            ProcessInstanceEventBody body = ((ProcessInstanceDataEvent) event).getData();

            Map<String, Object> variables = body.getVariables();

            Incident incident = (Incident) variables.get("incident");
            String supportTeam = (String) variables.get("assignee");
            String status = (String) variables.get("status");
            List<?> comments = (List<?>) variables.get("comments");

            Insert insertSupport = insertInto("automatiko", SUPPORT_TEAM_TABLE)
                    .value("SUPPORT_TEAM", literal(supportTeam))
                    .value("INCIDENT_ID", literal(body.getBusinessKey()))
                    .value("TITLE", literal(incident.getTitle()))
                    .value("DESCRIPTION", literal(incident.getDescription()))
                    .value("SEVERITY", literal(incident.getSeverity()))
                    .value("STATUS", literal(status))
                    .value("REPORTER", literal(incident.getReporter().getEmail()))
                    .value("REPORTER_COMPANY", literal(incident.getReporter().getCompany()));

            Insert insertReporter = insertInto("automatiko", REPORTER_TABLE)
                    .value("REPORTER", literal(incident.getReporter().getEmail()))
                    .value("INCIDENT_ID", literal(body.getBusinessKey()))
                    .value("TITLE", literal(incident.getTitle()))
                    .value("DESCRIPTION", literal(incident.getDescription()))
                    .value("SEVERITY", literal(incident.getSeverity()))
                    .value("STATUS", literal(status))
                    .value("NO_OF_COMMENTS", literal(comments.size()));

            try {
                cqlSession.execute(insertSupport.build());
                cqlSession.execute(insertReporter.build());
            } catch (QueryExecutionException e) {

            }
        }
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        events.forEach(this::publish);
    }

}
