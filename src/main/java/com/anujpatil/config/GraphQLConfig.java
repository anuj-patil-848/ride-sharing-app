package com.anujpatil.config;

import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class GraphQLConfig {

    /**
     * Configure the DateTime scalar for GraphQL
     * 
     * @return RuntimeWiringConfigurer with DateTime scalar
     */
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(dateTimeScalar());
    }

    /**
     * Create a custom scalar for DateTime
     * 
     * @return GraphQLScalarType for DateTime
     */
    @Bean
    public GraphQLScalarType dateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("Java LocalDateTime scalar")
                .coercing(new graphql.schema.Coercing<LocalDateTime, String>() {
                    @Override
                    public String serialize(Object dataFetcherResult) {
                        if (dataFetcherResult instanceof LocalDateTime) {
                            return ((LocalDateTime) dataFetcherResult).format(DateTimeFormatter.ISO_DATE_TIME);
                        }
                        throw new graphql.schema.CoercingSerializeException("Expected a LocalDateTime object.");
                    }

                    @Override
                    public LocalDateTime parseValue(Object input) {
                        if (input instanceof String) {
                            return LocalDateTime.parse((String) input, DateTimeFormatter.ISO_DATE_TIME);
                        }
                        throw new graphql.schema.CoercingParseValueException("Expected a String");
                    }

                    @Override
                    public LocalDateTime parseLiteral(Object input) {
                        if (input instanceof graphql.language.StringValue) {
                            String value = ((graphql.language.StringValue) input).getValue();
                            return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
                        }
                        throw new graphql.schema.CoercingParseLiteralException("Expected a StringValue.");
                    }
                })
                .build();
    }
}