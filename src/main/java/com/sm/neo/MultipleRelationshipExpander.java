package com.sm.neo;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.BranchState;

public class MultipleRelationshipExpander implements PathExpander {
    protected RelationshipType[] relationshipTypes;

    public MultipleRelationshipExpander(RelationshipType[] relationshipTypes){
        this.relationshipTypes = relationshipTypes;
    }

    @Override
    public Iterable<Relationship> expand(Path path, BranchState state) {
        return path.endNode().getRelationships(Direction.BOTH, relationshipTypes);
    }

    @Override
    public PathExpander reverse() {
        return null;
    }
}
