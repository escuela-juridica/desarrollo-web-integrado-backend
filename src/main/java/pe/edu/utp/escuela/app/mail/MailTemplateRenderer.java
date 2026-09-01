package pe.edu.utp.escuela.app.mail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

@Component
public class MailTemplateRenderer {

    private static final Pattern SAFE_TEMPLATE_PATH =
            Pattern.compile("^mail/[a-zA-Z0-9/_-]+\\.html$");
    private static final Pattern FIELD =
            Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_.-]+)\\s*}}", Pattern.MULTILINE);

    public String render(String templatePath, Map<String, ?> fields) {
        validateTemplatePath(templatePath);
        String template = readTemplate(templatePath);
        Matcher matcher = FIELD.matcher(template);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String fieldName = matcher.group(1);
            if (!fields.containsKey(fieldName) || fields.get(fieldName) == null) {
                throw new IllegalArgumentException(
                        "Falta el campo '" + fieldName + "' para la plantilla " + templatePath);
            }
            String escapedValue = HtmlUtils.htmlEscape(
                    fields.get(fieldName).toString(), StandardCharsets.UTF_8.name());
            matcher.appendReplacement(result, Matcher.quoteReplacement(escapedValue));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private void validateTemplatePath(String templatePath) {
        if (templatePath == null || !SAFE_TEMPLATE_PATH.matcher(templatePath).matches()) {
            throw new IllegalArgumentException(
                    "La plantilla debe ser un archivo HTML dentro de resources/mail");
        }
    }

    private String readTemplate(String templatePath) {
        ClassPathResource resource = new ClassPathResource(templatePath);
        if (!resource.exists()) {
            throw new IllegalArgumentException("No existe la plantilla " + templatePath);
        }
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo leer la plantilla " + templatePath, exception);
        }
    }
}
