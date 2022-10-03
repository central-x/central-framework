findBy
===

```graphql
query GroupQuery($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    groups {
        findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders){
            id
            name
            projects {
                id
                groupId
                name
            }
        }
    }
}
```