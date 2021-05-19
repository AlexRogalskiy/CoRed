# CoRed

[![Kotlin](https://img.shields.io/badge/kotlin-1.4-blue.svg)](http://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.kittinunf.cored/cored?label=Maven%20Central)](https://search.maven.org/artifact/com.github.kittinunf.cored/cored)

CoRed is Redux-like implementation that maintains the benefits of Redux's core idea without the
boilerplate. No more action types, action creators, switch statements or complicated setup. It is
Kotlin and it has coroutine supported right out-of-the-box. Also, yes, it is Kotlin Multiplatform
supported (https://kotlinlang.org/docs/mpp-intro.html)

## Features

CoRed is an opinionated way to setup the Redux implementation. Why we need CoRed? Redux is an
amazing state management tool. However, some might find it to be too complicated or hard to
use/understand, because the plain vanilla Redux-like implementation is quite boilerplate-y. There is
an even
[page](https://redux.js.org/recipes/reducing-boilerplate/) from the official Redux.js on how to
reduce the boilerplate. That's why we develop CoRed.

In CoRed's implementation, we are trying to solve the same problem with original Redux by
introducing the more friendly API, then translating that into Kotlin in order to be a bit more
accessible to mobile dev to use in their projects.

## Installation

Add mavenCentral into your dependencies' repositories configuration

```kotlin
repositories {
    mavenCentral()
    // ...
}
```

Then, just add the dependency to your `commonMain` dependencies

```kotlin
commonMain {
    dependencies {
        // ...
        implementation("com.github.kittinunf.cored:cored:«version»")
    }
}
``` 

## How to set this up?

Good question. Let's try to set up a minimal example
with [StoreAdapter](./cored/src/commonMain/kotlin/com/github/kittinunf/cored/StoreAdapter.kt) with
an ability to show a list data from the network.

Assuming that we have a Repository class that already connects to the API somewhere,
eg. [Comments](http://jsonplaceholder.typicode.com/comments), we can use it to fetch the data for
our store.

```kotlin
interface CommentRepository {
    suspend fun getComments(): List<String>
}
```

Redux with CoRed implementation (the setup part should be under 35~ lines)

```kotlin
class CommentsState(val comments: List<String>? = null) : State

object Load : Identifiable
class SetComments(val comments: List<String>?) : Identifiable

val repository: CommentRepository // get repository somewhere e.g. manually create, DI, or 3rd party library

val store = Store(
    scope = viewScope,
    initialState = CommentsState(),
    reducers = mapOf(
        "SetComments" to Reducer { currentState: CommentsState, action: SetComments ->
            currentState.copy(comments = action.comments)
        }
    ),
    middlewares = mapOf(
        "Load" to Middleware { order: Order, store: Store, state: CommentsState, action: Load ->
            if (order == Order.AfterReduced) {
                scope.launch {
                    val result = repository.getComments()
                    if (result.isSuccess) {
                        store.dispatch(SetComments(result.value))
                    } else {
                        store.dispatch(SetComments(null))
                    }
                }
            }
        }
    )
)

```

Usage

```kotlin

// in Coroutine scope - you can observe state changes with `collect`
store.states
    .collect {
        /** This should return { comments : [ {
        "postId": 1,
        "id": 1,
        "name": "id labore ex et quam laborum",
        "email": "Eliseo@gardner.biz",
        "body": "laudantium enim quasi est quidem magnam voluptate ipsam eos\ntempora quo necessitatibus\ndolor quam autem quasi\nreiciendis et nam sapiente accusantium"
        }, ... ] }
         **/
        println(it)
    }

// dispatch an action 
store.dispatch(Load)
```

For documentation, check more details in the [README](./cored/README.md)

For example, check more tests in the
[commonTest](./cored/src/commonTest/kotlin/com/github/kittinunf/cored/StoreAdapterTest.kt) folder.

## Credits

CoRed is brought to you by [contributors](https://github.com/kittinunf/CoRed/graphs/contributors).

## Licenses

CoRed is released under the [MIT](https://opensource.org/licenses/MIT) license.
