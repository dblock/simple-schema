package org.opensearch.languages.oql.graphql.wiring;

import graphql.execution.ExecutionStepInfo;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import org.opensearch.schema.SchemaError;
import org.opensearch.schema.ontology.EntityType;

import java.util.Optional;

import static org.opensearch.languages.oql.graphql.wiring.TranslationUtils.*;

/**
 * this translator is responsible of adding an entity with its predicates to the next query steps
 */
public class EntityWithPredicateTranslation implements QueryTranslationStrategy {

    @Override
    public Optional<Object> translate(QueryTranslatorContext context, GraphQLType fieldType) {
        ExecutionStepInfo parent = context.getEnv().getExecutionStepInfo().getParent();
        fieldType = extractConcreteFieldType(fieldType);
        if (fieldType instanceof GraphQLObjectType) {
            GraphQLObjectType type = (GraphQLObjectType) fieldType;
            GraphQLObjectType parentType = (GraphQLObjectType) parent.getType();
            //add the start query element
            if (parentType.getName().equalsIgnoreCase(QUERY)) {
                context.getBuilder().start();
            }
            //populate vertex or relation
            Optional<EntityType> realType = populateGraphObject(context, type.getName());
            try {
                addWhereClause(context, realType);
            } catch (Throwable e) {
                throw new SchemaError.SchemaErrorException("During GraphQL to Ontology QL translation, failed on EntityWithPredicateTranslation::addWhereClause",e);
            }
            //todo create concrete union types from abstract interface
            return Optional.of(new Object());
        }
        return Optional.empty();
    }
}
