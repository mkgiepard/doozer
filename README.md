# doozer
Dead simple UI testing framework.


## Syntax

Positional parameters:
```
<action>[?]
<action>[?] "<selector>"
<action>[?] "<selector>" "<options>"
```


Named parameters:

```
<action>[?]
<action>[?] selector:"<selector>"
<action>[?] args:"<options>"
<action>[?] selector:"<selector>" args:"<options>"
```

Examples:

```
click "By.cssSelector('button')"
click "By.cssSelector('button')" "button=2,offsetX=10,offsetY=10"

click selector:"By.cssSelector('button')"
click selector:"By.cssSelector('button')" args:"button=2,offsetX=10,offsetY=10"
click selector:"Sl({button 'Accept *'})"
```

`?` - optional parameter indicating the execution should proceed despite action failure

### Actions

- Assertions
  - `assertCurrentUrl`
  - `assertInnerText`
  - `assertPageTitle`

- Interactions
  - `alert`
  - `clear`
  - `click`
  - `contextClick`
  - `doubleClick`
  - `hover`
  - `hoverByOffset`
  - `keyDown`
  - `keyUp`
  - `select`
  - `sendKeys`
  - `type`

- Navigation
  - `navigateBack`
  - `navigateForward`
  - `navigateTo`
  - `refresh`
  - `url`

- Generics
  - `import`
  - `set`
  - `takeScreenshot`
  - `wait`
  - `waitForElement`

### Selectors

- `By.` - By selectors
- `Sl.` - Semantic locators
- `""` - empty selector, for actions that don't use it


### Options

One of:

- single parameter
- a list of `key=value` pairs separated with `,`.

## Launch parameters

- `doozer.directory` - defines a directory with doozer scripts
- `doozer.test` - defines a doozer script to run
- `doozer.browser` - defines a browser to use during test, default: `chrome-headless`
- `doozer.failOnPixelDiff` - when set to `false` all the `takeScreenshot` actions are treated as optional,
default: `true`
