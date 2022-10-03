findBy
===

```graphql
query PersonQuery($id: String) {
    result: persons {
        findById(id: $id){
            id
            name
            pets {
                id
                masterId
                name
            }
        }
    }
}
```


insert
===

```graphql
mutation PersonMutation($input: PersonInput, $operator: String) {
    result: persons {
        insert(input: $input, operator: $operator) {
            __typename
            id
            name
        }
    }
}
```