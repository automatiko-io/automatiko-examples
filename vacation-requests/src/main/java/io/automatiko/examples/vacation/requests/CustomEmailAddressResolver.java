package io.automatiko.examples.vacation.requests;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.automatiko.engine.addons.usertasks.email.EmailAddressResolver;

@ApplicationScoped
public class CustomEmailAddressResolver implements EmailAddressResolver {

    private Optional<String> to;

    @Inject
    public CustomEmailAddressResolver(@ConfigProperty(name = "email.to") Optional<String> to) {
        this.to = to;
    }

    @Override
    public Map<String, String> resolve(Collection<String> users, Collection<String> groups) {

        Map<String, String> emails = new HashMap<>();

        if (to.isPresent()) {
            if (users != null) {
                users.stream().forEach(s -> emails.put(s, to.get()));
            }
            if (groups != null) {
                groups.stream().forEach(s -> emails.put(s, to.get()));
            }
        }

        return emails;
    }

}
