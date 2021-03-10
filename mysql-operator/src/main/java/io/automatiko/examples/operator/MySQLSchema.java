package io.automatiko.examples.operator;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("mysql.sample.javaoperatorsdk")
@Version("v1")
public class MySQLSchema extends CustomResource<SchemaSpec, SchemaStatus> implements Namespaced {

    @Override
    protected SchemaSpec initSpec() {
        return new SchemaSpec();
    }

    @Override
    protected SchemaStatus initStatus() {
        return new SchemaStatus();
    }

}
