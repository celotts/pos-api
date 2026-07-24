import React from 'react'
import ReactDOM from 'react-dom/client'
import { Provider } from 'react-redux'
import { store } from './store'
import App from './App'
import AppLoader from './components/AppLoader';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <Provider store={store}>
      <AppLoader>
        <App />
      </AppLoader>
    </Provider>
  </React.StrictMode>,
)
