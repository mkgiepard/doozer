# doozer
Dead simple UI testing framework.


## Syntax

`<action>[?] "<selector>" "<options>"`

Examples:

```
click "By.cssSelector('button')"

type "By.name('title')" "doozer"
click "By.cssSelector('button')" "{button:2,offsetX:10,offsetY:10}"

click? "By.cssSelector('button')"

takeScreenshot "" "{name:mainPage,timeout:2}"
```

`?` - optional parameter indicating the execution should proceed despite action failure

### Actions

- Assertions
  - `assertCurrentUrl`
  - `assertInnerText`
  - `assertPageTitle`

- Interactions
  - `click`
  - `type`

- Navigation
  - `navigateBack`
  - `navigateForward`
  - `navigateTo`
  - `refresh`
  - `url`

- Generics
  - `takeScreenshot`

### Selectors

- `By.` - By selectors
- `Sl.` - Semantic locators
- `""` - empty selector, for actions that don't use it


### Options

One of:
- single parameter
- a map of `key=value` pairs separated with `,` surrounded by `{}`.

